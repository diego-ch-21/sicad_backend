package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_horario")
    private Integer idHorario;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false,
            foreignKey = @ForeignKey(name = "FK_HORARIO_CURSO"))
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = true,
            foreignKey = @ForeignKey(name = "FK_HORARIO_AULA"))
    private Aula aula;

    @Column(nullable = false, name = "tipo_sesion")
    private String tipoSesion;

    @Column(nullable = false, name = "dia_semana")
    private String diaSemana;

    @Column(nullable = false, name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(nullable = false, name = "hora_fin")
    private LocalTime horaFin;

    @Column(nullable = false, name = "duracion_horas")
    private Integer duracionHoras;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
