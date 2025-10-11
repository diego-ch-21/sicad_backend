package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.impl.AsignacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final AsignacionServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AsignacionDetalleResponse>> findAll() throws Exception {
        List<AsignacionDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Asignacions", lista)
        );
    }

    @GetMapping("/buscar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>  findById(@PathVariable("idAsignacion") Integer id) throws Exception {
        Asignacion obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Asignacion encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>> save(@Valid @RequestBody AsignacionCreateRequest request){
        BaseObjectResponse<AsignacionDetalleResponse> response = serviceImpl.registrarAsignacion(request);
        return ResponseEntity.status(response.status()).body(response);

    }

    @PutMapping("/actualizar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>> update(@Valid @PathVariable("idAsignacion") Integer id, @RequestBody AsignacionUpdateRequest request){
        BaseObjectResponse<AsignacionDetalleResponse> response = serviceImpl.actualizarAsignacion(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // NUEVO ENDPOINT PARA EL ALGORITMO HÍBRIDO GA + PSO
    @PostMapping("/algoritmo/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>> asignarConAlgoritmoHibrido(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) {

        try {

            BaseObjectResponse<CargaDetalleResponse> response =
                    serviceImpl.asignarConAlgoritmoGeneticoPSO(idCicloAcademico);
            return ResponseEntity.status(response.status()).body(response);

        } catch (IllegalArgumentException e) {
            log.info("Error de validación en algoritmo híbrido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseObjectResponse<>(400,
                            "Error de validación: " + e.getMessage(), null));

        } catch (Exception e) {
            log.info("Error crítico en algoritmo híbrido para carga electiva " + idCicloAcademico + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseObjectResponse<>(500,
                            "Error interno del servidor en algoritmo híbrido: " + e.getMessage(), null));
        }
    }

    @GetMapping("/listar/{idDocente}/{idCarga}")
    public ResponseEntity<BaseListReponse<AsignacionResumenResponse>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente,
            @PathVariable("idCarga") Integer idCarga) throws Exception {
        BaseListReponse<AsignacionResumenResponse> response = serviceImpl.obtenerAsignacionesPorDocenteCarga(idDocente, idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idAsignacion") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarAsignacion(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private AsignacionDetalleResponse convertToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDetalleResponse.class);
    }
}