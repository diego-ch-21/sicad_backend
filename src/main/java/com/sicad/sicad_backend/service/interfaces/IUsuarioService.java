package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IUsuarioService extends ICRUD<Usuario, Integer> {
    List<Usuario> findByEnabledTrue();

}
