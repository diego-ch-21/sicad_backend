package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especializacion")
@RequiredArgsConstructor
public class EspecializacionController {
    private final IEspecializacionService service;

    @GetMapping("/listar-por-docente/{idDocente}")
    public ResponseEntity<BaseListReponse<EspecializacionResumenResponse>>
            listar(@PathVariable("idDocente") Integer idDocente) throws Exception {
        BaseListReponse<EspecializacionResumenResponse> response = service.listarPorDocente(idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
            buscar(@PathVariable("idEspecializacion") Integer id) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
            registrar(@Valid @RequestBody EspecializacionCreateRequest request) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<EspecializacionDetalleResponse>>
            registrarAll(@Valid @RequestBody List<EspecializacionCreateRequest> request) {
        BaseListReponse<EspecializacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
            actualizar(@PathVariable("idEspecializacion") Integer id, @Valid @RequestBody EspecializacionUpdateRequest dto) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idEspecializacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
