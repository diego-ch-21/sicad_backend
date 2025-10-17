package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.Horario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IHorarioService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario")
@RequiredArgsConstructor
@Tag(name = "Horario", description = "Endpoints para la gestión de Horarios asociados a un Curso")
public class HorarioController {
    private final IHorarioService service;

    // --- LISTAR POR CURSO ---
    @Operation(
            summary = "Listar horarios por Curso",
            description = "Obtiene una lista de todos los horarios asociados a un curso específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente")
    })
    @GetMapping("/listar-por-curso/{idCurso}")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
    listar(@PathVariable("idCurso") Integer idCurso){
        BaseListReponse<HorarioDetalleResponse> response = service.listarPorCurso(idCurso);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar horario por ID",
            description = "Busca y obtiene los detalles de un horario específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
    buscar(@PathVariable("idHorario") Integer id) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) POR CURSO ---
    @Operation(
            summary = "Registrar horario para un Curso",
            description = "Crea y registra un nuevo horario, asociándolo al curso especificado por ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario registrado exitosamente")
    })
    @PostMapping("/insertar-por-curso/{idCurso}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
    registrar(@PathVariable("idCurso") Integer id,@Valid @RequestBody HorarioCreateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.registrarPorCurso(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) POR CURSO ---
    @Operation(
            summary = "Registrar múltiples horarios para un Curso",
            description = "Crea y registra múltiples horarios a partir de una lista, asociándolos al curso especificado por ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horarios registrados exitosamente")
    })
    @PostMapping("/insertar-all-por-curso/{idCurso}")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
    registrarAll(@PathVariable("idCurso") Integer id,@Valid @RequestBody List<HorarioCreateRequest> request) {
        BaseListReponse<HorarioDetalleResponse> response = service.registrarAllPorCurso(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar horario",
            description = "Actualiza los datos de un horario existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
    actualizar(@PathVariable("idHorario") Integer id, @Valid @RequestBody HorarioUpdateRequest dto) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar horario",
            description = "Elimina (lógicamente) un horario del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idHorario") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}