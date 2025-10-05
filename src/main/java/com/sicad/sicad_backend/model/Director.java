package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="director")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idDirector;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 6, name = "codigo")
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "id_escuela", nullable = true,
            foreignKey = @ForeignKey(name = "FK_DIRECTOR_ESCUELA"))
    private Escuela escuela;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
