package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.DepartamentoAcademico;
import com.sicad.sicad_backend.service.base.ICRUD;

public interface IDepartamentoAcademicoService extends ICRUD<DepartamentoAcademico, Integer> {

    BaseListReponse<DepartamentoAcademicoDetalleResponse> listar();

    BaseObjectResponse<DepartamentoAcademicoDetalleResponse> buscar(Integer idDirector);

    BaseObjectResponse<DepartamentoAcademicoDetalleResponse> registrar(DepartamentoAcademicoCreateRequest request);


    BaseObjectResponse<DepartamentoAcademicoDetalleResponse> actualizar(Integer idDirector, DepartamentoAcademicoUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idDirector);
}
