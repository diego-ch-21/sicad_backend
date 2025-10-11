package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IAlgoritmoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/algoritmo")
@RequiredArgsConstructor
public class AlgoritmoController{
    private final  IAlgoritmoService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AlgoritmoDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<AlgoritmoDetalleResponse> response = service.listar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
            buscar(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
            registrar(@Valid @RequestBody AlgoritmoCreateRequest request) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AlgoritmoDetalleResponse>>
            registrarAll(@Valid @RequestBody List<AlgoritmoCreateRequest> request) {
        BaseListReponse<AlgoritmoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
            actualizar(@PathVariable("idAlgoritmo") Integer id, @Valid @RequestBody AlgoritmoUpdateRequest dto) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/principal/seleccionar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
            asignarPrincipal(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.asignarPrincipal(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/principal/buscar")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
            buscarPrincipal(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.buscarPrincipal();
        return ResponseEntity.ok(response);
    }
}
