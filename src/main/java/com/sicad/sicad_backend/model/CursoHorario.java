package com.sicad.sicad_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="curso_horario")
public class CursoHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_curso_horario")
    private Integer idCursoHorario;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_HORARIO_CURSO"))
    private Curso curso;

    @OneToOne
    @JoinColumn(name = "id_aula", nullable = true)
    private Aula aula;

    @Column(nullable = false, name = "tipo_sesion")
    private String tipoSesion;

    @Column(nullable = false, name = "dia_semana")
    private String diaSemana;

    @Column(nullable = false, name = "hora_inicio")
    private Time horaInicio;

    @Column(nullable = false, name = "hora_fin")
    private Time horaFin;

    @Column(nullable = false, name = "duracion_horas")
    private Integer duracionHoras;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
