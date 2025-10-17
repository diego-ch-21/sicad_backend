package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
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
@RequestMapping("/docente")
@RequiredArgsConstructor
@Tag(name = "Docente", description = "Endpoints para la gestión e integración de datos de Docentes")
public class DocenteController {
    private final IDocenteService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar docentes",
            description = "Obtiene una lista de todos los docentes registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DocenteDetalleResponse>>
    listar() {
        BaseListReponse<DocenteDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar docente por ID",
            description = "Busca y obtiene los detalles de un docente específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
    buscar(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR USUARIO ---
    @Operation(
            summary = "Buscar docente por ID de Usuario",
            description = "Busca y obtiene los detalles del docente asociado a un ID de usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado para el ID de usuario",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar-por-usuario/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
    buscarPorUsuario(@PathVariable("idUsuario") Integer id) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.buscarPorUsuario(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar docente",
            description = "Crea y registra un nuevo docente en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Docente registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
    registrar(@Valid @RequestBody DocenteCreateRequest request) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples docentes",
            description = "Crea y registra múltiples docentes en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Docentes registrados exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DocenteDetalleResponse>>
    registrarAll(@Valid @RequestBody List<DocenteCreateRequest> request) {
        BaseListReponse<DocenteDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar docente",
            description = "Actualiza los datos de un docente existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DocenteDetalleResponse>>
    actualizar(@PathVariable("idDocente") Integer id, @Valid @RequestBody DocenteUpdateRequest dto) {
        BaseObjectResponse<DocenteDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar docente",
            description = "Elimina (lógicamente) un docente del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Docente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR CON ESPECIALIZACIONES ---
    @Operation(
            summary = "Listar docentes con especializaciones",
            description = "Obtiene una lista de todos los docentes, incluyendo sus especializaciones registradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes y sus especializaciones obtenida exitosamente")
    })
    @GetMapping("/listar-con-especializaciones")
    public ResponseEntity<BaseListReponse<DocenteEspecializacionResponse>>
    docenteEspecializacion(){
        BaseListReponse<DocenteEspecializacionResponse> response = service.listarDocentesConEspecializaciones();
        return  ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR CON PREFERENCIAS ---
    @Operation(
            summary = "Listar docentes con preferencias por Ciclo Académico",
            description = "Obtiene una lista de docentes con sus preferencias de asignación para un ciclo académico específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes y preferencias obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo Académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-con-preferencias/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<DocentePreferenciaResponse>>
    docentesPreferencias(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<DocentePreferenciaResponse> response = service.listarDocentesConPreferencias(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR CON DISPONIBILIDADES ---
    @Operation(
            summary = "Listar docentes con disponibilidades por Ciclo Académico",
            description = "Obtiene una lista de docentes con su disponibilidad horaria registrada para un ciclo académico específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes y disponibilidades obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo Académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-con-disponibilidades/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<DocenteDisponibilidadResponse>>
    docentesDisponibilidad(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<DocenteDisponibilidadResponse> response = service.listarDocentesConDisponibilidad(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- LISTAR CON ASIGNACIONES ---
    @Operation(
            summary = "Listar docentes con asignaciones por Carga",
            description = "Obtiene una lista de docentes con sus asignaciones resultantes para una Carga (ejecución del algoritmo) específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes y asignaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carga de Asignación no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-con-asignaciones/{idCarga}")
    public ResponseEntity<BaseListReponse<DocenteAsignacionResponse>>
    docentesAsignacionesCicloAcademicoAndCarga(
            @PathVariable("idCarga") Integer idCarga) throws Exception {
        BaseListReponse<DocenteAsignacionResponse> response = service.listarDocentesCargaConAsignaciones(idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }
}