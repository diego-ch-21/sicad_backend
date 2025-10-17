package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignacion.*;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignacion")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Asignación", description = "Endpoints para la gestión de Asignaciones y la ejecución del Algoritmo")
public class AsignacionController {
    private final IAsignacionService service;

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar asignación por ID",
            description = "Busca y obtiene los detalles de una asignación específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
    buscar(@PathVariable("idAsignacion") Integer id) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar asignación",
            description = "Crea y registra una nueva asignación en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignación registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
    registrar(@Valid @RequestBody AsignacionCreateRequest request) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples asignaciones",
            description = "Crea y registra múltiples asignaciones en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignaciones registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AsignacionDetalleResponse>>
    registrarAll(@Valid @RequestBody List<AsignacionCreateRequest> request) {
        BaseListReponse<AsignacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar asignación",
            description = "Actualiza los datos de una asignación existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignación actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<AsignacionDetalleResponse>>
    actualizar(@PathVariable("idAsignacion") Integer id, @Valid @RequestBody AsignacionUpdateRequest dto) {
        BaseObjectResponse<AsignacionDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar asignación",
            description = "Elimina (lógicamente) una asignación del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idAsignacion}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idAsignacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- EJECUTAR ALGORITMO ---
    @Operation(
            summary = "Ejecutar algoritmo de asignación",
            description = "Ejecuta el Algoritmo (Genético/PSO) sobre todos los cursos de un ciclo académico. El resultado es una nueva Carga (idCarga) con las asignaciones generadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo ejecutado y asignaciones guardadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la ejecución del algoritmo o datos insuficientes",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PostMapping("/algoritmo-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>> asignarConAlgoritmoGeneticoPSO(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) {
        BaseObjectResponse<CargaDetalleResponse> response = service.asignarConAlgoritmoGeneticoPSO(idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR POR CARGA Y DOCENTE ---
    @Operation(
            summary = "Listar asignaciones por Carga y Docente",
            description = "Obtiene la lista de asignaciones resultantes para un docente específico dentro de una carga de asignación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida exitosamente")
    })
    @GetMapping("/listar-carga-docente/{idCarga}/{idDocente}")
    public ResponseEntity<BaseListReponse<AsignacionResumenResponse>>
    listarPorDocenteCarga(
            @PathVariable("idCarga") Integer idCarga,
            @PathVariable("idDocente") Integer idDocente) {
        BaseListReponse<AsignacionResumenResponse> response = service.listarPorDocenteCarga(idDocente, idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR POR CARGA ---
    @Operation(
            summary = "Listar asignaciones por Carga",
            description = "Obtiene la lista completa de asignaciones para una Carga específica, ordenada por ciclo, asignatura y grupo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida exitosamente")
    })
    @GetMapping("/listar-carga/{idCarga}")
    public ResponseEntity<BaseListReponse<AsignacionCicloResumenResponse>>
    listarPorCarga(
            @PathVariable("idCarga") Integer idCarga){
        BaseListReponse<AsignacionCicloResumenResponse> response = service.listarPorCarga(idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR POR CARGA Y ESCUELA ---
    @Operation(
            summary = "Listar asignaciones por Carga y Escuela",
            description = "Obtiene la lista de asignaciones para una Carga específica, filtrada por Escuela. La lista está ordenada por ciclo, asignatura y grupo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida exitosamente")
    })
    @GetMapping("/listar-carga-escuela/{idCarga}/{idEscuela}")
    public ResponseEntity<BaseListReponse<AsignacionCicloResumenResponse>>
    listarPorCargaEscuela(
            @PathVariable("idCarga") Integer idCarga,
            @PathVariable("idEscuela") Integer idEscuela) {
        BaseListReponse<AsignacionCicloResumenResponse> response = service.listarPorCargaEscuela(idCarga, idEscuela);
        return ResponseEntity.status(response.status()).body(response);
    }
}