-- =====================================================================
-- V2: datos de prueba (5 usuarios, 20 incidencias y su historial de estados)
-- Las fechas son relativas a now() para que los datos parezcan "recientes".
-- =====================================================================

-- ---------------------------------------------------------------------
-- Usuarios: 1 administrador, 2 técnicos (resuelven) y 2 usuarios (reportan).
-- Dominio example.com: reservado para ejemplos, nunca será un correo real.
-- ---------------------------------------------------------------------
INSERT INTO usuario (nombre, email, rol) VALUES
    ('Ana García',     'ana.garcia@example.com',     'ADMIN'),
    ('Carlos Ruiz',    'carlos.ruiz@example.com',    'TECNICO'),
    ('Lucía Martín',   'lucia.martin@example.com',   'TECNICO'),
    ('Javier López',   'javier.lopez@example.com',   'USUARIO'),
    ('Sara Fernández', 'sara.fernandez@example.com', 'USUARIO');

-- ---------------------------------------------------------------------
-- Incidencias.
-- En lugar de escribir ids a mano (1, 2, 3...), cada fila indica el email del creador y del
-- asignado, y un JOIN con usuario obtiene el id real. Así el script no depende de qué ids
-- haya generado la secuencia. "dias" = hace cuántos días se creó la incidencia.
-- ---------------------------------------------------------------------
INSERT INTO incidencia (titulo, descripcion, estado, prioridad, creador_id, asignado_id, fecha_creacion)
SELECT v.titulo,
       v.descripcion,
       v.estado,
       v.prioridad,
       creador.id,
       asignado.id,
       now() - make_interval(days => v.dias)
FROM (VALUES
    -- ABIERTA: recién reportadas, algunas aún sin técnico asignado
    ('No arranca el portátil tras actualizar Windows', 'Se queda en la pantalla de carga después de la actualización de anoche.', 'ABIERTA', 'ALTA', 'javier.lopez@example.com', NULL, 1),
    ('La impresora de la planta 2 no imprime', 'Los trabajos se quedan en cola y no sale nada.', 'ABIERTA', 'MEDIA', 'sara.fernandez@example.com', NULL, 2),
    ('Acceso a la carpeta compartida de RRHH', 'Necesito permisos de lectura para la carpeta de nóminas.', 'ABIERTA', 'BAJA', 'sara.fernandez@example.com', 'carlos.ruiz@example.com', 3),
    ('Error 500 al exportar informes a PDF', 'Falla con informes de más de 50 páginas.', 'ABIERTA', 'ALTA', 'ana.garcia@example.com', 'lucia.martin@example.com', 1),
    ('El correo no sincroniza en el móvil', 'Desde el cambio de contraseña el móvil no recibe correos.', 'ABIERTA', 'MEDIA', 'javier.lopez@example.com', NULL, 4),
    ('La VPN se desconecta cada pocos minutos', 'Afecta a todo el equipo que teletrabaja.', 'ABIERTA', 'CRITICA', 'ana.garcia@example.com', 'carlos.ruiz@example.com', 0),
    ('Parpadeo en el monitor externo', 'Solo ocurre con el cable HDMI, con DisplayPort va bien.', 'ABIERTA', 'BAJA', 'javier.lopez@example.com', NULL, 6),
    -- EN_PROGRESO: un técnico está trabajando en ellas
    ('Caída del servidor de ficheros', 'Las unidades de red no responden.', 'EN_PROGRESO', 'CRITICA', 'ana.garcia@example.com', 'carlos.ruiz@example.com', 5),
    ('Lentitud en el ERP por las mañanas', 'Entre las 9 y las 10 cada pantalla tarda más de 30 segundos.', 'EN_PROGRESO', 'ALTA', 'sara.fernandez@example.com', 'lucia.martin@example.com', 8),
    ('No se puede restablecer la contraseña desde la intranet', 'El enlace del correo de recuperación da error.', 'EN_PROGRESO', 'MEDIA', 'javier.lopez@example.com', 'carlos.ruiz@example.com', 6),
    ('Instalar licencia de software de diseño', 'Licencia nueva para el equipo de marketing.', 'EN_PROGRESO', 'BAJA', 'sara.fernandez@example.com', 'lucia.martin@example.com', 10),
    ('Falla la copia de seguridad nocturna', 'El log indica falta de espacio en el destino.', 'EN_PROGRESO', 'ALTA', 'ana.garcia@example.com', 'lucia.martin@example.com', 4),
    -- RESUELTA: el técnico la dio por solucionada, pendiente de que el creador la cierre
    ('El teclado inalámbrico no responde', 'Tras cambiar las pilas sigue sin funcionar.', 'RESUELTA', 'BAJA', 'javier.lopez@example.com', 'carlos.ruiz@example.com', 12),
    ('Certificado SSL de la web interna caducado', 'El navegador muestra aviso de sitio no seguro.', 'RESUELTA', 'CRITICA', 'ana.garcia@example.com', 'lucia.martin@example.com', 15),
    ('El calendario compartido no muestra reuniones', 'Las reuniones creadas por otros no aparecen.', 'RESUELTA', 'MEDIA', 'sara.fernandez@example.com', 'carlos.ruiz@example.com', 9),
    ('Error al adjuntar ficheros grandes', 'El gestor documental rechaza ficheros de más de 10 MB.', 'RESUELTA', 'MEDIA', 'javier.lopez@example.com', 'lucia.martin@example.com', 20),
    -- CERRADA: ciclo completo terminado
    ('Alta de usuario para nuevo empleado', 'Crear cuenta, correo y acceso al ERP.', 'CERRADA', 'MEDIA', 'ana.garcia@example.com', 'carlos.ruiz@example.com', 30),
    ('Sin cobertura wifi en la sala de reuniones', 'La sala grande no tiene señal.', 'CERRADA', 'ALTA', 'sara.fernandez@example.com', 'carlos.ruiz@example.com', 25),
    ('El ratón hace doble clic solo', 'Se abren ficheros sin querer.', 'CERRADA', 'BAJA', 'javier.lopez@example.com', 'lucia.martin@example.com', 40),
    ('Fallo en el cálculo de horas extra de la nómina', 'Las horas nocturnas no se suman.', 'CERRADA', 'CRITICA', 'ana.garcia@example.com', 'lucia.martin@example.com', 35)
) AS v (titulo, descripcion, estado, prioridad, email_creador, email_asignado, dias)
JOIN usuario creador ON creador.email = v.email_creador
-- LEFT JOIN porque el asignado puede ser NULL (incidencia sin técnico todavía).
LEFT JOIN usuario asignado ON asignado.email = v.email_asignado;

-- ---------------------------------------------------------------------
-- Historial coherente con el estado actual de cada incidencia.
-- Ciclo de vida: ABIERTA -> EN_PROGRESO -> RESUELTA -> CERRADA
-- Cada paso se registra un día después del anterior.
-- ---------------------------------------------------------------------

-- 1) ABIERTA -> EN_PROGRESO: lo hace el técnico asignado al empezar a trabajar.
INSERT INTO historial_incidencia (incidencia_id, usuario_id, campo, valor_anterior, valor_nuevo, fecha_cambio)
SELECT id, asignado_id, 'ESTADO', 'ABIERTA', 'EN_PROGRESO', fecha_creacion + INTERVAL '1 day'
FROM incidencia
WHERE estado IN ('EN_PROGRESO', 'RESUELTA', 'CERRADA');

-- 2) EN_PROGRESO -> RESUELTA: lo hace el técnico al solucionarla.
INSERT INTO historial_incidencia (incidencia_id, usuario_id, campo, valor_anterior, valor_nuevo, fecha_cambio)
SELECT id, asignado_id, 'ESTADO', 'EN_PROGRESO', 'RESUELTA', fecha_creacion + INTERVAL '2 days'
FROM incidencia
WHERE estado IN ('RESUELTA', 'CERRADA');

-- 3) RESUELTA -> CERRADA: lo hace quien la abrió, confirmando que está solucionada.
INSERT INTO historial_incidencia (incidencia_id, usuario_id, campo, valor_anterior, valor_nuevo, fecha_cambio)
SELECT id, creador_id, 'ESTADO', 'RESUELTA', 'CERRADA', fecha_creacion + INTERVAL '3 days'
FROM incidencia
WHERE estado = 'CERRADA';

-- La fecha de última actualización de cada incidencia es la de su último cambio de estado
-- (las que no tienen historial conservan fecha_actualizacion = fecha_creacion).
UPDATE incidencia i
SET fecha_actualizacion = h.ultimo_cambio
FROM (SELECT incidencia_id, max(fecha_cambio) AS ultimo_cambio
      FROM historial_incidencia
      GROUP BY incidencia_id) h
WHERE h.incidencia_id = i.id;

UPDATE incidencia
SET fecha_actualizacion = fecha_creacion
WHERE id NOT IN (SELECT incidencia_id FROM historial_incidencia);
