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
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DIRECTOR_USUARIO"))
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_facultad", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DIRECTOR_FACULTAD"))
    private Facultad facultad;
    @Column(nullable = false, name = "cargo")
    private String cargo;
    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
