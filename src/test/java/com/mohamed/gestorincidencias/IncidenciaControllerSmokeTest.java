package com.mohamed.gestorincidencias;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de humo: arranca la aplicación completa contra la BD real (Docker Compose) y comprueba
 * que el listado responde. No comprueba el número exacto de filas para no romperse cuando
 * se creen incidencias nuevas.
 * En Spring Boot 4, @AutoConfigureMockMvc está en org.springframework.boot.webmvc.test.autoconfigure
 * (en Boot 3 estaba en org.springframework.boot.test.autoconfigure.web.servlet).
 */
@SpringBootTest
@AutoConfigureMockMvc
class IncidenciaControllerSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarIncidenciasDevuelve200YDatos() throws Exception {
        mockMvc.perform(get("/api/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].reportador.nombre").isString());
    }
}
