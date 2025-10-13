package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.dto.docente.DocenteDetalleResponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.service.impl.CargaServiceImpl;
import com.sicad.sicad_backend.service.impl.DocenteServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import com.sicad.sicad_backend.service.report.pdf.IPdfGneratorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carga")
@RequiredArgsConstructor
public class CargaController {

    private final ICargaService service;
    private final CargaServiceImpl serviceImpl;
    private final ModelMapper modelMapper;
    private final IPdfGneratorService pdfService;
    private final DocenteServiceImpl docenteService;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CargaDetalleResponse>> findAll() throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<CargaDetalleResponse>>
        findAllCicloAcademico(@PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrueAndCicloAcademico_Id(idCicloAcademico)
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("buscar/{idCarga}")
    public ResponseEntity<GenericObjectResponse<CargaDetalleResponse>>  findByIdCarga(@PathVariable("idCarga") Integer id) throws Exception {
        Carga obj  = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Carga encontrada", convertToDetalle(obj))
        );
    }
    @GetMapping("principal/buscar/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<CargaDetalleResponse>>  obtenerCargaPrincipal(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        GenericObjectResponse<CargaDetalleResponse> response = serviceImpl.obtenerCargaDefecto(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("principal/insertar/{idCicloAcademico}/{idCarga}")
    public ResponseEntity<GenericObjectResponse<CargaDetalleResponse>>
            insertarCargaPrincipal(@PathVariable("idCicloAcademico") Integer idCarga,@PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        GenericObjectResponse<CargaDetalleResponse> response = serviceImpl.insertarCargaDefecto(idCicloAcademico,idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idCarga}")
    public ResponseEntity<GenericObjectResponse<String>>
            delete(@PathVariable("idCarga") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarCarga(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/exportar/pdf/carga-electiva/{idCarga}")
    public ResponseEntity<byte[]>
    exportarCargaElectiva(@PathVariable("idCarga") Integer idCarga) {
        GenericReponse<DocenteAsignacionResponse> docentesResponse = docenteService.listarDocentesCargaConAsignaciones(idCarga);
        List<DocenteAsignacionResponse> docentes = docentesResponse.data();
        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }
        GenericObjectResponse<CargaDetalleResponse> cargaResponse = service.buscar(idCarga);
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

        GenericReponse<DocenteAsignacionResponse> docentesResponse =
                docenteService.listarDocentesCargaConAsignaciones(idCarga);

        if (docentesResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

        List<DocenteAsignacionResponse> docentes = docentesResponse.data();

        GenericObjectResponse<CargaDetalleResponse> cargaResponse = service.buscar(idCarga);
        CargaDetalleResponse carga = cargaResponse.data();
        if (cargaResponse.status() != 200) {
            return ResponseEntity.status(docentesResponse.status()).build();
        }

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



    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}
