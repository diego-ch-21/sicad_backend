package com.sicad.sicad_backend.service.report.pdf;

import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;

import java.util.List;

public interface IPdfGneratorService {
    byte[] generarPdfCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
    byte[] generarExcelCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
}
