package com.mohamed.gestorincidencias.dto;

import com.mohamed.gestorincidencias.dominio.Estado;
import com.mohamed.gestorincidencias.dominio.Incidencia;
import com.mohamed.gestorincidencias.dominio.Prioridad;

import java.time.Instant;

/**
 * Lo que la API devuelve de una incidencia. Es un record inmutable, independiente de la entidad:
 * cambiar la tabla no cambia el contrato JSON, y no hay relaciones LAZY que fallen al serializar.
 */
public record IncidenciaResponse(
        Long id,
        String titulo,
        String descripcion,
        Estado estado,
        Prioridad prioridad,
        UsuarioResumen reportador,
        UsuarioResumen responsable,
        Instant fechaCreacion,
        Instant fechaActualizacion) {

    /** Debe llamarse dentro de una transacción si los usuarios no se cargaron con la consulta. */
    public static IncidenciaResponse de(Incidencia incidencia) {
        return new IncidenciaResponse(
                incidencia.getId(),
                incidencia.getTitulo(),
                incidencia.getDescripcion(),
                incidencia.getEstado(),
                incidencia.getPrioridad(),
                UsuarioResumen.de(incidencia.getReportador()),
                UsuarioResumen.de(incidencia.getResponsable()),
                incidencia.getFechaCreacion(),
                incidencia.getFechaActualizacion());
    }
}
