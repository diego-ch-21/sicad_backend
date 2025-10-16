package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferencia")
@RequiredArgsConstructor
public class PreferenciaController {
    private final IPreferenciaService service;

    @GetMapping("/listar/{idDocente}/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<PreferenciaResumenResponse>>
            listar(@PathVariable("idDocente") Integer idDocente,
                   @PathVariable("idCicloAcademico") Integer idCicloAcademico) {
        BaseListReponse<PreferenciaResumenResponse> response = service.listarPorDocenteCicloAcademico(idDocente,idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
            buscar(@PathVariable("idPreferencia") Integer id) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
            registrar(@Valid @RequestBody PreferenciaCreateRequest request) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<PreferenciaDetalleResponse>>
            registrarAll(@Valid @RequestBody List<PreferenciaCreateRequest> request) {
        BaseListReponse<PreferenciaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
            actualizar(@PathVariable("idPreferencia") Integer id, @Valid @RequestBody PreferenciaUpdateRequest dto) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idPreferencia") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
