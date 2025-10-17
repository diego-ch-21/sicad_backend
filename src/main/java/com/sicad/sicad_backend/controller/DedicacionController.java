package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dedicacion")
@RequiredArgsConstructor
public class DedicacionController {

    private final IDedicacionService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<DedicacionDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
            buscar(@PathVariable("idDedicacion") Integer id) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar-por-docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
            buscarPorDocente(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.buscarPorDocente(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
            registrar(@Valid @RequestBody DedicacionCreateRequest request) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>>
            registrarAll(@Valid @RequestBody List<DedicacionCreateRequest> request) {
        BaseListReponse<DedicacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
            actualizar(@PathVariable("idDedicacion") Integer id,
                       @Valid @RequestBody DedicacionUpdateRequest request) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.actualizar(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<String>>
        eliminar(@PathVariable("idDedicacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}
