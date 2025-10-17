package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.Enum.Message;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
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
@Tag(name = "Carga")
public class CargaController {

    private final ICargaService service;

    @Operation(summary = "Listar cargas de un ciclo académico")
    @GetMapping("/listar-por-ciclo-academico/{idCicloAcademico}")
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

    @PutMapping("/principal-asignar-por-ciclo-academico/{idCicloAcademico}/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            asignarPrincipal(@PathVariable("idCicloAcademico") Integer idCicloAcademico, @PathVariable("idCarga") Integer idCarga) {
        BaseObjectResponse<CargaDetalleResponse> response = service.asignarPrincipal(idCicloAcademico, idCarga);
        return ResponseEntity.status(response.status()).body(response);    }

    @GetMapping("/principal-buscar-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            buscarPrincipal(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CargaDetalleResponse> response = service.buscarPrincipal(id);
        return ResponseEntity.status(response.status()).body(response);    }



}
