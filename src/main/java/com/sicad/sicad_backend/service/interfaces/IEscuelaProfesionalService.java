package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalCreateRequest;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalDetalleResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUpdateRequest;
import com.sicad.sicad_backend.model.EscuelaProfesional;
import com.sicad.sicad_backend.service.base.ICRUD;

public interface IEscuelaProfesionalService  extends ICRUD<EscuelaProfesional, Integer>  {

        BaseListReponse<EscuelaProfesionalDetalleResponse> listar();

        BaseObjectResponse<EscuelaProfesionalDetalleResponse> buscar(Integer idDirector);

        BaseObjectResponse<EscuelaProfesionalDetalleResponse> registrar(EscuelaProfesionalCreateRequest request);


        BaseObjectResponse<EscuelaProfesionalDetalleResponse> actualizar(Integer idDirector, EscuelaProfesionalUpdateRequest request);

        BaseObjectResponse<String> eliminar(Integer idDirector);
    
}
