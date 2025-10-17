package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.service.report.IReporteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reporte")
@RequiredArgsConstructor
@Tag(name = "reporte")
public class ReporteController {
    private final ICargaService cargaService;
    private final IReporteService pdfService;
    private final IDocenteService docenteService;

    @PostMapping("/exportar-pdf/carga-electiva-all/{idCarga}")
    public ResponseEntity<byte[]>
            exportarCargaElectiva(@PathVariable("idCarga") Integer idCarga) {
        BaseListReponse<DocenteAsignacionResponse> docentesResponse = docenteService.listarDocentesCargaConAsignaciones(idCarga);
        List<DocenteAsignacionResponse> docentes = docentesResponse.data();
        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        BaseObjectResponse<CargaDetalleResponse> cargaResponse = cargaService.buscar(idCarga);
        CargaDetalleResponse carga = cargaResponse.data();
        if (cargaResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        byte[] pdfBytes = pdfService.generarPdfCargaElectiva(docentes, carga);
        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-carga-electiva.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/exportar-excel/carga-electiva-all/{idCarga}")
    public ResponseEntity<byte[]>
            exportarExcelCargaElectivaDocente(@PathVariable("idCarga") Integer idCarga) {

        BaseListReponse<DocenteAsignacionResponse> docentesResponse =
                docenteService.listarDocentesCargaConAsignaciones(idCarga);

        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        List<DocenteAsignacionResponse> docentes = docentesResponse.data();

        BaseObjectResponse<CargaDetalleResponse> cargaResponse = cargaService.buscar(idCarga);

        if (cargaResponse.status() != 200) {
            return ResponseEntity.status(cargaResponse.status()).build();
        }

        CargaDetalleResponse carga = cargaResponse.data();

        // Generar el archivo Excel
        byte[] excelBytes = pdfService.generarExcelCargaElectiva(docentes, carga);

        if (excelBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        // Tipo MIME correcto para archivos Excel
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        // Nombre del archivo con extensión .xlsx
        headers.setContentDispositionFormData("attachment",
                "reporte-carga-electiva-" + carga.getCicloAcademico().getNombre() + ".xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
