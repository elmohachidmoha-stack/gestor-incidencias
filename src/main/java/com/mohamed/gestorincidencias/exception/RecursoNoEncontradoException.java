package com.mohamed.gestorincidencias.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando se pide algo que no existe (incidencia, usuario...).
 * @ResponseStatus hace que Spring responda 404 sin más código. El Día 3 un
 * @RestControllerAdvice la capturará para devolver el error en un JSON uniforme.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
