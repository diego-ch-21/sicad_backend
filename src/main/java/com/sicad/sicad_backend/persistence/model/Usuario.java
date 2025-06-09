package com.sicad.sicad_backend.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="usuario")
public class Usuario {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name="id_rol", nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private Rol rol;
    @Column(nullable = false, length = 50, name = "nombre")
    private String username;
    @Column(nullable = false,length = 60,name="password")
    private String password;
    @Column(nullable = false,name="enabled")
    private boolean enabled;
    @Column(nullable = false, length = 50, name = "nombres")
    private String nombres;
    @Column(nullable = false, length = 50, name = "apellidos")
    private String apellidos;
    @Column(nullable = false, length = 100, name = "email")
    private String email;
}
