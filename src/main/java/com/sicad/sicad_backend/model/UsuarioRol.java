package com.sicad.sicad_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="usuario_rol")
@IdClass(UsuarioRolPK.class)
public class UsuarioRol {
    private Integer idUsuario;
    private Integer idRol;
    private Boolean enable;
}
