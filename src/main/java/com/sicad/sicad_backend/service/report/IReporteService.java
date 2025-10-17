package com.sicad.sicad_backend.service.report;

import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;

import java.util.List;

public interface IReporteService {
    byte[] generarPdfCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
    byte[] generarExcelCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
}
