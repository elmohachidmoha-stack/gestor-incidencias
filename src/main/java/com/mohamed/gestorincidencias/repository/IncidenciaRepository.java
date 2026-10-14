package com.mohamed.gestorincidencias.repository;

import com.mohamed.gestorincidencias.dominio.Incidencia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    /**
     * Lista todas las incidencias con su reportador y su responsable en UNA sola consulta.
     * El @EntityGraph carga las relaciones LAZY con JOIN solo en esta consulta (LEFT JOIN para
     * responsable, que puede ser NULL);
     * sin él, al leer los usuarios de N incidencias se lanzarían N consultas extra (problema N+1).
     */
    @EntityGraph(attributePaths = {"reportador", "responsable"})
    List<Incidencia> findAllByOrderByIdAsc();

    /** Igual que findById, pero trayendo también los dos usuarios en la misma consulta. */
    @EntityGraph(attributePaths = {"reportador", "responsable"})
    Optional<Incidencia> findConUsuariosById(Long id);
}
