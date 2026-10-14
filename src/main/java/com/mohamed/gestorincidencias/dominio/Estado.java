package com.mohamed.gestorincidencias.dominio;

/**
 * Estados del ciclo de vida de una incidencia.
 * Los valores coinciden exactamente con el CHECK ck_incidencia_estado de V1__esquema_inicial.sql:
 * si se añade uno aquí, hay que añadirlo también en la BD con una migración nueva.
 */
public enum Estado {
    ABIERTA,
    EN_PROGRESO,
    RESUELTA,
    CERRADA
}
