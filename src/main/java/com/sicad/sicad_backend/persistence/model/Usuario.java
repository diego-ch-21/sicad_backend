package com.sicad.sicad_backend.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="usuario")
public class Usuario implements UserDetails {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_ROL"))
    private Rol idRol;

    @Basic
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((idRol.getNombre())));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
