package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.service.report.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Reporte", description = "Endpoints para la generación y exportación de reportes (PDF/Excel)")
public class ReporteController {
    private final ICargaService cargaService;
    private final IReporteService pdfService; // Nombre un poco confuso, maneja PDF y Excel
    private final IDocenteService docenteService;

    // --- EXPORTAR PDF CARGA ELECTIVA POR DOCENTE ---
    @Operation(
            summary = "Exportar PDF de Carga Electiva por Docente",
            description = "Genera y exporta un reporte en formato PDF que detalla la carga electiva (asignaciones) de todos los docentes para una Carga de Asignación específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte PDF generado y descargado exitosamente",
                    content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "La Carga de Asignación no fue encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al generar el PDF")
    })
    @PostMapping("/exportar-pdf/carga-electiva-por-docente/{idCarga}")
    public ResponseEntity<byte[]>
    exportarCargaElectiva(@PathVariable("idCarga") Integer idCarga) {
        BaseListReponse<DocenteAsignacionResponse> docentesResponse = docenteService.listarDocentesCargaConAsignaciones(idCarga);

        if (docentesResponse.status() != 200) {
            // Si el servicio devuelve un 404, se propaga el status
            return ResponseEntity.status(docentesResponse.status()).build();
        }
        List<DocenteAsignacionResponse> docentes = docentesResponse.data();


        BaseObjectResponse<CargaDetalleResponse> cargaResponse = cargaService.buscar(idCarga);
        if (cargaResponse.status() != 200) {
            // Si el servicio devuelve un 404, se propaga el status
            return ResponseEntity.status(cargaResponse.status()).build();
        }
        CargaDetalleResponse carga = cargaResponse.data();

        byte[] pdfBytes = pdfService.generarPdfCargaElectiva(docentes, carga);
        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-carga-electiva.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // --- EXPORTAR EXCEL CARGA ELECTIVA POR DOCENTE ---
    @Operation(
            summary = "Exportar Excel de Carga Electiva por Docente",
            description = "Genera y exporta un reporte en formato Excel (XLSX) que detalla la carga electiva (asignaciones) de todos los docentes para una Carga de Asignación específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte Excel generado y descargado exitosamente",
                    content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "La Carga de Asignación no fue encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al generar el Excel")
    })
    @PostMapping("/exportar-excel/carga-electiva-por-docente/{idCarga}")
    public ResponseEntity<byte[]>
    exportarExcelCargaElectivaDocente(@PathVariable("idCarga") Integer idCarga) {

        BaseListReponse<DocenteAsignacionResponse> docentesResponse =
                docenteService.listarDocentesCargaConAsignaciones(idCarga);

        if (docentesResponse.status() != 200) {
            // Si el servicio devuelve un 404, se propaga el status
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        List<DocenteAsignacionResponse> docentes = docentesResponse.data();

        BaseObjectResponse<CargaDetalleResponse> cargaResponse = cargaService.buscar(idCarga);

        if (cargaResponse.status() != 200) {
            // Si el servicio devuelve un 404, se propaga el status
            return ResponseEntity.status(cargaResponse.status()).build();
        }

        CargaDetalleResponse carga = cargaResponse.data();

        // Generar el archivo Excel
        byte[] excelBytes = pdfService.generarExcelCargaElectiva(docentes, carga);

        if (excelBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        // Tipo MIME correcto para archivos Excel XLSX
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        // Nombre del archivo dinámico
        headers.setContentDispositionFormData("attachment",
                "reporte-carga-electiva-" + carga.getCicloAcademico().getNombre() + ".xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}