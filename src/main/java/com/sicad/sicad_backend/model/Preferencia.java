package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "preferencia",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_docente", "id_curso"}))
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencia")
    private Integer idPreferencia;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_docente",
            foreignKey = @ForeignKey(name = "PREFERENCIA_DOCENTE_CURSO_DOC"))
    private Docente docente;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_curso",
            foreignKey = @ForeignKey(name = "PREFERENCIA_DOCENTE_CURSO_CUR"))
    private Curso curso;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
