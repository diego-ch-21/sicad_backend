package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaUpdateRequest;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.service.interfaces.IEscuelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/escuela")
@RequiredArgsConstructor
public class EscuelaController {

    private final IEscuelaService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<EscuelaDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<EscuelaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
            buscar(@PathVariable("idEscuela") Integer id) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
            registrar(@Valid @RequestBody EscuelaCreateRequest request) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<EscuelaDetalleResponse>>
            registrarAll(@Valid @RequestBody List<EscuelaCreateRequest> request) {
        BaseListReponse<EscuelaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
            actualizar(@PathVariable("idEscuela") Integer id, @Valid @RequestBody EscuelaUpdateRequest dto) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idEscuela") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}