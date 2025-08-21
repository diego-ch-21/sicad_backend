package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "especializacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_ESPECIALIZACION_ASIGNATURA_DOCENTE",
                        columnNames = {"id_asignatura", "id_docente"}
                )
        }
)
public class Especializacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especializacion")
    private Integer idEspecializacion;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_asignatura",
            foreignKey = @ForeignKey(name = "FK_ESPECIALIZACION_ASIGNATURA"))
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ESPECIALIZACION_DOCENTE"))
    private Docente docente;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
