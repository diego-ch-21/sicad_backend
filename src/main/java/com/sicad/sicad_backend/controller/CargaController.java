package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carga")
@RequiredArgsConstructor
@Tag(name = "Carga", description = "Endpoints para la gestión e historial de Cargas de Asignación")
public class CargaController {

    private final ICargaService service;

    // --- LISTAR POR CICLO ACADÉMICO ---
    @Operation(
            summary = "Listar cargas de un ciclo académico",
            description = "Obtiene el historial de todas las cargas de asignación generadas para un ciclo académico específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cargas obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CargaDetalleResponse>>
    listar(@PathVariable("idCicloAcademico") Integer id){
        BaseListReponse<CargaDetalleResponse> response = service.listarPorCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar carga por ID",
            description = "Busca y obtiene los detalles de una carga de asignación específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carga no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
    buscar(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<CargaDetalleResponse> response = service.buscar(id);
        // Corregido para usar el status de la respuesta
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar carga",
            description = "Elimina (lógicamente) una carga de asignación usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carga no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ASIGNAR PRINCIPAL ---
    @Operation(
            summary = "Asignar carga principal para ciclo académico",
            description = "Establece una carga de asignación específica (idCarga) como la carga principal activa para un ciclo académico dado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga asignada como principal exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carga o Ciclo Académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/principal-asignar-por-ciclo-academico/{idCicloAcademico}/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
    asignarPrincipal(@PathVariable("idCicloAcademico") Integer idCicloAcademico, @PathVariable("idCarga") Integer idCarga) {
        BaseObjectResponse<CargaDetalleResponse> response = service.asignarPrincipal(idCicloAcademico, idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR PRINCIPAL ---
    @Operation(
            summary = "Buscar carga principal de ciclo académico",
            description = "Obtiene los detalles de la carga de asignación que está marcada como principal y activa para el ciclo académico especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga principal encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico o Carga Principal no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/principal-buscar-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
    buscarPrincipal(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CargaDetalleResponse> response = service.buscarPrincipal(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}