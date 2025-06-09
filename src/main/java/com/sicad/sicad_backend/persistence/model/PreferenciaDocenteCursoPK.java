package com.sicad.sicad_backend.persistence.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class PreferenciaDocenteCursoPK implements Serializable {
    @ManyToOne
    @JoinColumn(nullable = false,name="id_docente",
            foreignKey = @ForeignKey(name = "PREFERENCIA_DOCENTE_CURSO_DOC"))
    private Docente docente;
    @ManyToOne
    @JoinColumn(nullable = false,name="id_curso",
            foreignKey = @ForeignKey(name = "PREFERENCIA_DOCENTE_CURSO_CUR"))
    private Curso curso;
}
