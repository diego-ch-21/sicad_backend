package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="disponibilidad")
public class Disponibilidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idDisponibilidad;
    @ManyToOne
    @JoinColumn(name = "id_docente",nullable = false,
            foreignKey = @ForeignKey(name = "FK_DISPONIBILIDAD_DOCENTE"))
    private Docente docente;
    @Column(nullable = false, name = "dia_semana")
    private String diaSemana;
    @Column(nullable = false, name = "hora_inicio")
    private Time horaInicio;
    @Column(nullable = false, name = "hora_fin")
    private Time horaFin;
}
