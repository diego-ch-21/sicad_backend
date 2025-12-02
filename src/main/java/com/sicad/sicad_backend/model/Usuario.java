package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="usuario")
public class Usuario {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rol",nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ROL"))
    private Rol rol;

    @Column(nullable = false,name="password")
    private String password;

    @Column(nullable = false, name = "email")
    @Email
    private String email;
    @Column(name="url_perfil")
    private String urlPerfil;

    @Column(nullable = false, length = 8, name = "codigo")
    private String codigo;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = true, name = "apellido")
    private String apellido;

    @Column(nullable = false, name = "created_at")
    private LocalDate cretedAt;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;

}
