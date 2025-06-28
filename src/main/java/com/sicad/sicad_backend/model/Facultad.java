package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "facultad")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_facultad")
    private Integer idFacultad;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
