package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asignacion")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_docente",
            foreignKey = @ForeignKey(name = "FK_ASIGNACION_DOCENTE"))
    private Docente docente;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_curso",
            foreignKey = @ForeignKey(name = "FK_ASIGNACION_CURSO"))
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ASIGNACION_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @ManyToOne
    @JoinColumn(name = "id_carga", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ASIGNACION_CARGA"))
    private Carga carga;

    @Column(nullable = true, name = "tipo_asignacion")
    private String tipoAsignacion;

    @Column(nullable = false, name = "created_at")
    private LocalDate createdAt;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
