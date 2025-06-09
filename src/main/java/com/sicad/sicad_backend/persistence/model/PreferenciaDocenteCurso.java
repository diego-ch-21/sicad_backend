package com.sicad.sicad_backend.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="preferencia_docente_curso")
@IdClass(PreferenciaDocenteCursoPK.class)
public class PreferenciaDocenteCurso {
    @Id
    private Docente docente;
    @Id
    private Curso curso;
}
