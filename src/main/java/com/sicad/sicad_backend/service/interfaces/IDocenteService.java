package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDocenteService extends ICRUD<Docente, Integer> {
    BaseListReponse<DocenteDetalleResponse> listar();

    BaseObjectResponse<DocenteDetalleResponse> buscar(Integer idDocente);

    BaseObjectResponse<DocenteDetalleResponse> buscarPorUsuario(Integer idUsuario);

    BaseObjectResponse<DocenteDetalleResponse> registrar(DocenteCreateRequest request);

    BaseListReponse<DocenteDetalleResponse> registrarAll(List<DocenteCreateRequest> requests);

    BaseObjectResponse<DocenteDetalleResponse> actualizar(Integer idDocente, DocenteUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idDocente);

    BaseListReponse<DocenteEspecializacionResponse> listarDocentesConEspecializaciones();

    BaseListReponse<DocentePreferenciaResponse> listarDocentesConPreferencias(Integer idCicloAcademico);

    BaseListReponse<DocenteDisponibilidadResponse> listarDocentesConDisponibilidad(Integer idCicloAcademico);

    BaseListReponse<DocenteAsignacionResponse> listarDocentesCargaConAsignaciones(Integer idCarga);
}
