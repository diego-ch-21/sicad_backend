package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.dto.asignacion.*;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAsignacionService extends ICRUD<Asignacion, Integer> {

    BaseObjectResponse<AsignacionDetalleResponse> buscar(Integer idAsignacion);

    BaseObjectResponse<AsignacionDetalleResponse> registrar(AsignacionCreateRequest request);

    BaseListReponse<AsignacionDetalleResponse> registrarAll(List<AsignacionCreateRequest> requests);

    BaseObjectResponse<AsignacionDetalleResponse> actualizar(Integer idAsignacion, AsignacionUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idAsignacion);

    BaseListReponse<AsignacionResumenResponse> listarPorDocenteCarga(Integer idDocente, Integer idCarga);

    BaseListReponse<AsignacionCicloResumenResponse> listarPorCargaEscuela(Integer idCarga,Integer idEscuela);

    BaseListReponse<AsignacionCicloResumenResponse> listarPorCarga(Integer idCarga);


    BaseObjectResponse<CargaDetalleResponse> asignarConAlgoritmoGeneticoPSO(Integer idCicloAcademico);

}
