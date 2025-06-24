package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "escuela")
public class Escuela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_escuela")
    private Integer idEscuela;

    @Column(nullable = false, unique = true, length = 8, name = "codigo")
    private String codigo;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_facultad", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ESCUELA_FACULTAD"))
    private Facultad facultad;

    @Column(nullable = false,name="enabled")
    private boolean enabled;
}
