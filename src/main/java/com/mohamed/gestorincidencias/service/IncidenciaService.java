package com.mohamed.gestorincidencias.service;

import com.mohamed.gestorincidencias.dominio.Incidencia;
import com.mohamed.gestorincidencias.dominio.Prioridad;
import com.mohamed.gestorincidencias.dominio.Usuario;
import com.mohamed.gestorincidencias.dto.CrearIncidenciaRequest;
import com.mohamed.gestorincidencias.dto.IncidenciaResponse;
import com.mohamed.gestorincidencias.exception.RecursoNoEncontradoException;
import com.mohamed.gestorincidencias.repository.IncidenciaRepository;
import com.mohamed.gestorincidencias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de las incidencias. Devuelve DTOs, nunca entidades: la conversión se hace
 * aquí, dentro de la transacción, porque con open-in-view=false la sesión de Hibernate se cierra
 * al salir del service y el controller ya no podría cargar relaciones LAZY.
 */
@Service
// Por defecto todos los métodos son de solo lectura; los que escriben lo sobrescriben.
// readOnly permite a Hibernate no vigilar cambios en las entidades (menos trabajo y memoria).
@Transactional(readOnly = true)
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor: dependencias final, explícitas y fáciles de simular en tests.
    public IncidenciaService(IncidenciaRepository incidenciaRepository, UsuarioRepository usuarioRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<IncidenciaResponse> listar() {
        return incidenciaRepository.findAllByOrderByIdAsc().stream()
                .map(IncidenciaResponse::de)
                .toList();
    }

    public IncidenciaResponse obtener(Long id) {
        return incidenciaRepository.findConUsuariosById(id)
                .map(IncidenciaResponse::de)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la incidencia con id " + id));
    }

    @Transactional
    public IncidenciaResponse crear(CrearIncidenciaRequest peticion) {
        Usuario reportador = usuarioRepository.findById(peticion.reportadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + peticion.reportadorId()));

        // Si no se indica prioridad se usa MEDIA, igual que el DEFAULT de la columna en V1.
        Prioridad prioridad = peticion.prioridad() != null ? peticion.prioridad() : Prioridad.MEDIA;

        Incidencia incidencia = new Incidencia(peticion.titulo(), peticion.descripcion(), prioridad, reportador);
        return IncidenciaResponse.de(incidenciaRepository.save(incidencia));
    }
}
