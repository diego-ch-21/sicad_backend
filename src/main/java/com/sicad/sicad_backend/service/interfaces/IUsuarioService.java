package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.service.base.ICRUD;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService extends ICRUD<Usuario, Integer> {
    BaseListReponse<UsuarioDetalleResponse> listar();
    BaseObjectResponse<UsuarioDetalleResponse> buscar(Integer idUsuario);

}
