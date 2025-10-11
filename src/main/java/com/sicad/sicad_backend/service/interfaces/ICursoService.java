package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICursoService extends ICRUD<Curso, Integer> {
    List<Curso> findByEnabledTrue();
    BaseListReponse<CursoDetalleResponse> listarCursosConHorarios();
    BaseListReponse<CursoDetalleResponse> listarCursosPorCicloAcademico(Integer idCicloAcademico);
    BaseObjectResponse<CursoDetalleResponse> registrarCurso(CursoCreateRequest request);
    BaseObjectResponse<CursoDetalleResponse> actualizarCurso(Integer idCurso, CursoUpdateRequest request);
    BaseListReponse<CursoDetalleResponse> registrarCursosMultiples(List<CursoCreateRequest> requests);
    BaseObjectResponse<String> eliminarCurso(Integer idCurso);
}
