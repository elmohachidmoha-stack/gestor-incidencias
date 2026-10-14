package com.mohamed.gestorincidencias.controller;

import com.mohamed.gestorincidencias.dto.CrearIncidenciaRequest;
import com.mohamed.gestorincidencias.dto.IncidenciaResponse;
import com.mohamed.gestorincidencias.service.IncidenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Capa HTTP: traduce peticiones a llamadas al service y resultados a respuestas HTTP.
 * No contiene lógica de negocio ni toca repositorios directamente.
 */
@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @GetMapping
    public List<IncidenciaResponse> listar() {
        return incidenciaService.listar();
    }

    @GetMapping("/{id}")
    public IncidenciaResponse obtener(@PathVariable Long id) {
        return incidenciaService.obtener(id);
    }

    /** Responde 201 Created con la cabecera Location apuntando a la nueva incidencia. */
    @PostMapping
    public ResponseEntity<IncidenciaResponse> crear(@RequestBody CrearIncidenciaRequest peticion) {
        IncidenciaResponse creada = incidenciaService.crear(peticion);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }
}
