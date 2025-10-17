package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalCreateRequest;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalDetalleResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IEscuelaProfesionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/escuela-profesional")
@RequiredArgsConstructor
public class EscuelaProfesionalController {
    private final IEscuelaProfesionalService service;
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<EscuelaProfesionalDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<EscuelaProfesionalDetalleResponse> response = service.listar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
            buscar(@PathVariable("idEscuelaProfesional") Integer id) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
            registrar(@Valid @RequestBody EscuelaProfesionalCreateRequest request) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
            actualizar(@PathVariable("idEscuelaProfesional") Integer id, @Valid @RequestBody EscuelaProfesionalUpdateRequest dto) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idEscuelaProfesional") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
