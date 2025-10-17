package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignacion.*;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignacion")
@RequiredArgsConstructor
@Slf4j
public class AsignacionController {
    private final IAsignacionService service;

    @GetMapping("/buscar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
            buscar(@PathVariable("idAsignacion") Integer id) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
            registrar(@Valid @RequestBody AsignacionCreateRequest request) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AsignacionDetalleResponse>>
            registrarAll(@Valid @RequestBody List<AsignacionCreateRequest> request) {
        BaseListReponse<AsignacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
            actualizar(@PathVariable("idAsignacion") Integer id, @Valid @RequestBody AsignacionUpdateRequest dto) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idAsignacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }


    @PostMapping("/algoritmo-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>> asignarConAlgoritmoGeneticoPSO(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) {
        BaseObjectResponse<CargaDetalleResponse> response = service.asignarConAlgoritmoGeneticoPSO(idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }


    @GetMapping("/listar-carga-docente/{idCarga}/{idDocente}")
    public ResponseEntity<BaseListReponse<AsignacionResumenResponse>>
            listarPorDocenteCarga(
                    @PathVariable("idCarga") Integer idCarga,
                    @PathVariable("idDocente") Integer idDocente) {
        BaseListReponse<AsignacionResumenResponse> response = service.listarPorDocenteCarga(idDocente, idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar-carga/{idCarga}")
    public ResponseEntity<BaseListReponse<AsignacionCicloResumenResponse>>
    listarPorCarga(
            @PathVariable("idCarga") Integer idCarga){
        BaseListReponse<AsignacionCicloResumenResponse> response = service.listarPorCarga(idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar-carga-escuela/{idCarga}/{idEscuela}")
    public ResponseEntity<BaseListReponse<AsignacionCicloResumenResponse>>
            listarPorCargaEscuela(
            @PathVariable("idCarga") Integer idCarga,
            @PathVariable("idEscuela") Integer idEscuela) {
        BaseListReponse<AsignacionCicloResumenResponse> response = service.listarPorCargaEscuela(idCarga, idEscuela);
        return ResponseEntity.status(response.status()).body(response);
    }





}