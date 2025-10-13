package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICursoService extends ICRUD<Curso, Integer> {
    BaseListReponse<CursoDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico);

    BaseObjectResponse<CursoDetalleResponse> buscar(Integer idCurso);

    BaseObjectResponse<CursoDetalleResponse> registrar(CursoCreateRequest request);

    BaseListReponse<CursoDetalleResponse> registrarAll(List<CursoCreateRequest> requests);

    BaseObjectResponse<CursoDetalleResponse> actualizar(Integer idCurso, CursoUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idCurso);

}
