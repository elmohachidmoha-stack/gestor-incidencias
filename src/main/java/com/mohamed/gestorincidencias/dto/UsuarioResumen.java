package com.mohamed.gestorincidencias.dto;

import com.mohamed.gestorincidencias.dominio.Usuario;

/**
 * Lo mínimo de un usuario que necesita quien consulta una incidencia.
 * No se exponen email, rol ni activo: la API solo devuelve lo que hace falta.
 */
public record UsuarioResumen(Long id, String nombre) {

    /** Devuelve null si no hay usuario (p. ej. una incidencia sin responsable). */
    public static UsuarioResumen de(Usuario usuario) {
        return usuario == null ? null : new UsuarioResumen(usuario.getId(), usuario.getNombre());
    }
}
