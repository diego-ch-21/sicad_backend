package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;


import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ILogisticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica")
@RequiredArgsConstructor
public class LogisticaController {
    private final ILogisticaService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<LogisticaDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<LogisticaDetalleResponse> response = service.listar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
            buscar(@PathVariable("idLogistica") Integer id) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
            registrar(@Valid @RequestBody LogisticaCreateRequest request) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<LogisticaDetalleResponse>>
            registrarAll(@Valid @RequestBody List<LogisticaCreateRequest> request) {
        BaseListReponse<LogisticaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
            actualizar(@PathVariable("idLogistica") Integer id, @Valid @RequestBody LogisticaUpdateRequest dto) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idLogistica") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
