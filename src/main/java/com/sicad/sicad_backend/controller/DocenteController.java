package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.report.pdf.PdfGeneratorServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {
    private final IDocenteService service;
    private final PdfGeneratorServiceImpl pdfService;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DocenteDetalleResponse>>
            listar() {
        BaseListReponse<DocenteDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
            buscar(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/usuario/buscar/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
            buscarPorUsuario(@PathVariable("idUsuario") Integer id) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.buscarPorUsuario(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
            registrar(@Valid @RequestBody DocenteCreateRequest request) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DocenteDetalleResponse>>
            registrarAll(@Valid @RequestBody List<DocenteCreateRequest> request) {
        BaseListReponse<DocenteDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
            actualizar(@PathVariable("idDocente") Integer id, @Valid @RequestBody DocenteUpdateRequest dto) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/especializaciones")
    public ResponseEntity<BaseListReponse<DocenteEspecializacionResponse>>
            docenteEspecializacion(){
        BaseListReponse<DocenteEspecializacionResponse> response = service.listarDocentesConEspecializaciones();
        return  ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/preferencias/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<DocentePreferenciaResponse>>
            docentesPreferencias(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<DocentePreferenciaResponse> response = service.listarDocentesConPreferencias(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/disponibilidades/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<DocenteDisponibilidadResponse>>
            docentesDisponibilidad(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<DocenteDisponibilidadResponse> response = service.listarDocentesConDisponibilidad(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/asignaciones/{idCarga}")
    public ResponseEntity<BaseListReponse<DocenteAsignacionResponse>>
    docentesAsignacionesCicloAcademicoAndCarga(
            @PathVariable("idCarga") Integer idCarga) throws Exception {
        BaseListReponse<DocenteAsignacionResponse> response = service.listarDocentesCargaConAsignaciones(idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }



}