package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAulaService extends ICRUD<Aula, Integer> {
    BaseListReponse<AulaDetalleResponse> listar();

    BaseObjectResponse<AulaDetalleResponse> buscar(Integer idAula);

    BaseObjectResponse<AulaDetalleResponse> registrar(AulaCreateRequest request);

    BaseListReponse<AulaDetalleResponse> registrarAll(List<AulaCreateRequest> requests);

    BaseObjectResponse<AulaDetalleResponse> actualizar(Integer idAula, AulaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idAula);}
