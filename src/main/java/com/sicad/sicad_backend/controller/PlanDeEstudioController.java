package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan-de-estudio")
@RequiredArgsConstructor
public class PlanDeEstudioController {

    private final IPlanDeEstudioService service;
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<PlanDeEstudioDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<PlanDeEstudioDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
            buscar(@PathVariable("idPlanDeEstudio") Integer id) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
            registrar(@Valid @RequestBody PlanDeEstudioCreateRequest request) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<PlanDeEstudioDetalleResponse>>
            registrarAll(@Valid @RequestBody List<PlanDeEstudioCreateRequest> request) {
        BaseListReponse<PlanDeEstudioDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
            actualizar(@PathVariable("idPlanDeEstudio") Integer id, @Valid @RequestBody PlanDeEstudioUpdateRequest dto) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idPlanDeEstudio") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}