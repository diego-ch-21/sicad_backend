package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="horario_curso")
public class HorarioCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idHorario;
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false,
            foreignKey = @ForeignKey(name = "FK_HORARIO_CURSO_ASIGNACION"))
    private Curso curso;
    private String grupo;
    private String tipoSesion;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String aula;
    private Integer duracionHoras;
}
