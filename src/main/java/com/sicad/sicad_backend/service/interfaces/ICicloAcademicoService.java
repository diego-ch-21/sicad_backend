package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoFileResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.service.base.ICRUD;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICicloAcademicoService extends ICRUD<CicloAcademico, Integer> {
    BaseListReponse<CicloAcademicoDetalleResponse> listar();

    BaseObjectResponse<CicloAcademicoDetalleResponse> buscar(Integer idCicloAcademico);

    BaseObjectResponse<CicloAcademicoFileResponse> buscarFile(Integer idCicloAcademico);

    BaseObjectResponse<CicloAcademicoDetalleResponse> registrar(CicloAcademicoCreateRequest request);

    BaseListReponse<CicloAcademicoDetalleResponse> registrarAll(List<CicloAcademicoCreateRequest> requests);

    BaseObjectResponse<CicloAcademicoDetalleResponse> actualizar(Integer idCicloAcademico, CicloAcademicoUpdateRequest request);

    BaseObjectResponse<CicloAcademicoFileResponse> actualizarFilePdf(Integer idCicloAcademico, MultipartFile filePdf);

    BaseObjectResponse<CicloAcademicoFileResponse> actualizarFileExcel(Integer idCicloAcademico, MultipartFile fileExcel);

    BaseObjectResponse<String> eliminar(Integer idCicloAcademico);
}
