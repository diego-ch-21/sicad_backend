package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.service.impl.PlanDeEstudioServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan_de_estudio")
@RequiredArgsConstructor
public class PlanDeEstudioController {

    private final IPlanDeEstudioService service;
    private final PlanDeEstudioServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<PlanDeEstudioDetalleResponse>> findAll() throws Exception {
        List<PlanDeEstudioDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Planes de Estudio", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<PlanDeEstudioDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        PlanDeEstudio obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Plan de Estudio encontrado", convertToResponseDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<PlanDeEstudioDetalleResponse>> registrar(@Valid @RequestBody PlanDeEstudioCreateRequest request) {
        GenericObjectResponse<PlanDeEstudioDetalleResponse> response = serviceImpl.registrarPlan(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<PlanDeEstudioDetalleResponse>> registrarAll(@Valid @RequestBody List<PlanDeEstudioCreateRequest> requestAll) {
        GenericReponse<PlanDeEstudioDetalleResponse> response = serviceImpl.registrarPlanesMultiples(requestAll);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<PlanDeEstudioDetalleResponse>> actualizar(@PathVariable("id") Integer id, @Valid @RequestBody PlanDeEstudioUpdateRequest dto) {
        GenericObjectResponse<PlanDeEstudioDetalleResponse> response = serviceImpl.actualizarPlan(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private PlanDeEstudioDetalleResponse convertToResponseDTO(PlanDeEstudio obj) {
        return modelMapper.map(obj, PlanDeEstudioDetalleResponse.class);
    }
}