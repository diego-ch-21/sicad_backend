package com.sicad.sicad_backend.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="rol")
public class Rol {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id_rol")
    private Integer idRol;
    @Column(nullable = false, length = 50, name = "nombre")
    private String nombre;
    @Column(nullable = false,name="enabled")
    private boolean enabled;
}
