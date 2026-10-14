package com.mohamed.gestorincidencias.dto;

import com.mohamed.gestorincidencias.dominio.Prioridad;

/**
 * Cuerpo del POST /api/incidencias. El estado no se recibe: toda incidencia nace ABIERTA.
 * reportadorId llega en el cuerpo porque todavía no hay autenticación; cuando la haya,
 * el reportador será el usuario autenticado.
 * Las anotaciones de validación (@NotBlank, @Size...) se añaden el Día 3.
 */
public record CrearIncidenciaRequest(
        String titulo,
        String descripcion,
        Prioridad prioridad,
        Long reportadorId) {
}
