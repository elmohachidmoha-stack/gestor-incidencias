package com.mohamed.gestorincidencias.repository;

import com.mohamed.gestorincidencias.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
