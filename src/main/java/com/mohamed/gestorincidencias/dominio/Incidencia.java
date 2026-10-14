package com.mohamed.gestorincidencias.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Incidencia: el elemento central de la aplicación. Mapea la tabla "incidencia" de V1.
 */
@Entity
@Table(name = "incidencia")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    // En la BD es TEXT (sin límite de longitud); columnDefinition hace que validate lo compare bien.
    @Column(columnDefinition = "text")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    // En la BD la columna se llama creador_id; en Java usamos el término de negocio "reportador".
    // LAZY: el usuario no se carga hasta que se necesita. Las consultas que sí lo necesitan
    // lo traen explícitamente con @EntityGraph (ver IncidenciaRepository) para evitar el N+1.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario reportador;

    // asignado_id puede ser NULL: una incidencia recién abierta puede no tener técnico todavía.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_id")
    private Usuario responsable;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    protected Incidencia() {
    }

    /** Toda incidencia nueva nace ABIERTA y sin responsable. */
    public Incidencia(String titulo, String descripcion, Prioridad prioridad, Usuario reportador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.reportador = reportador;
        this.estado = Estado.ABIERTA;
    }

    // Las fechas las pone la aplicación (no el DEFAULT now() de la BD) para que el objeto Java
    // las tenga en memoria justo después de guardar y la respuesta del POST no salga con null.
    @PrePersist
    void alCrear() {
        Instant ahora = Instant.now();
        this.fechaCreacion = ahora;
        this.fechaActualizacion = ahora;
    }

    // Se ejecuta antes de cada UPDATE: así ninguna modificación futura olvida actualizar la fecha.
    @PreUpdate
    void alActualizar() {
        this.fechaActualizacion = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public Usuario getReportador() {
        return reportador;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}
