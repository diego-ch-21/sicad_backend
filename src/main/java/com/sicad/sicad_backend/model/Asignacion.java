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
@Table(name = "asignacion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_docente", "id_horario"}))
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_docente",
            foreignKey = @ForeignKey(name = "ASIGNACION_DOCENTE"))
    private Docente docente;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_horario",
            foreignKey = @ForeignKey(name = "ASIGNACION_HORARIO"))
    private Curso horario;

    @Column(nullable = false, name = "tipo_asignacion")
    private String tipoAsignacion;

    @Column(nullable = false, name = "fecha_asignacion")
    private LocalDate fechaAsignacion;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
