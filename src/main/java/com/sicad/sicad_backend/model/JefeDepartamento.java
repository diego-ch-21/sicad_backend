package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="jefe_departamento")
public class JefeDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idJefeDepartamento;

    @OneToOne
    @JoinColumn(name = "id_usuario",nullable = false, unique = true)
    private Usuario usuario;
    @Column(nullable = false, name = "cargo")
    private String cargo;
    @Column(nullable = false, name = "enabled")
    private boolean enabled;

}
