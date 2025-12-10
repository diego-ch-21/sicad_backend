package com.sicad.sicad_backend.service.report;

import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoAgrupadoResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCicloResumenResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;

import java.util.List;

public interface IReporteService {
    byte[] generarPdfCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
    byte[] generarExcelCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga);
    byte[] generarPdfCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga);
    byte[] generarPdfCursosAgrupadoCarga(List<CursoAgrupadoResponse> data,CargaDetalleResponse carga,EscuelaDetalleResponse escuela);
    byte[] generarExcelCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga);
    byte[] generarExcelCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga, EscuelaDetalleResponse escuela);
}
