package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IRolService extends ICRUD<Rol, Integer> {
    BaseListReponse<RolDetalleResponse> listar();
    BaseObjectResponse<RolDetalleResponse> buscar(Integer idRol);
}
