package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.service.report.pdf.IPdfGneratorService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carga")
@RequiredArgsConstructor
@Tag(name = "Carga")
public class CargaController {

    private final ICargaService service;
    private final IPdfGneratorService pdfService;
    private final IDocenteService docenteService;

    @Operation(
            summary = "Listar cargas de un ciclo académico",
            description = "Obtiene todas las cargas registradas para el ciclo académico indicado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa. Devuelve la lista de cargas."),
            @ApiResponse(responseCode = "404", description = "Carga no encontrada", content = @Content(schema = @Schema(implementation = BaseListReponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content =@Content(schema = @Schema(implementation = BaseListReponse.class)))
    })
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CargaDetalleResponse>>
            listar(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<CargaDetalleResponse> response = service.listarPorCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            buscar(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<CargaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/principal/asignar/{idCicloAcademico}/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            asignarPrincipal(@PathVariable("idCicloAcademico") Integer idCicloAcademico, @PathVariable("idCarga") Integer idCarga) {
        BaseObjectResponse<CargaDetalleResponse> response = service.asignarPrincipal(idCicloAcademico, idCarga);
        return ResponseEntity.status(response.status()).body(response);    }

    @GetMapping("/principal/buscar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            buscarPrincipal(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CargaDetalleResponse> response = service.buscarPrincipal(id);
        return ResponseEntity.status(response.status()).body(response);    }

    @PostMapping("/exportar/pdf/carga-electiva/{idCarga}")
    public ResponseEntity<byte[]>
            exportarCargaElectiva(@PathVariable("idCarga") Integer idCarga) {
        BaseListReponse<DocenteAsignacionResponse> docentesResponse = docenteService.listarDocentesCargaConAsignaciones(idCarga);
        List<DocenteAsignacionResponse> docentes = docentesResponse.data();
        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        BaseObjectResponse<CargaDetalleResponse> cargaResponse = service.buscar(idCarga);
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

    @PostMapping("/exportar/excel/carga-electiva/{idCarga}")
    public ResponseEntity<byte[]> exportarExcelCargaElectivaDocente(
            @PathVariable("idCarga") Integer idCarga) {

        BaseListReponse<DocenteAsignacionResponse> docentesResponse =
                docenteService.listarDocentesCargaConAsignaciones(idCarga);

        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        List<DocenteAsignacionResponse> docentes = docentesResponse.data();

        BaseObjectResponse<CargaDetalleResponse> cargaResponse = service.buscar(idCarga);

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
