package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.persistence.model.Usuario;
import com.sicad.sicad_backend.service.base.ICRUD;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUsuarioService extends ICRUD<Usuario, Integer> {
}
