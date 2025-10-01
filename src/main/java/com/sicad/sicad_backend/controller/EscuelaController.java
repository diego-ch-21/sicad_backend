package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaUpdateRequest;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.service.impl.EscuelaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IEscuelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/escuela")
@RequiredArgsConstructor
public class EscuelaController {

    private final IEscuelaService service;
    private final EscuelaServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<EscuelaDetalleResponse>> findAll() throws Exception {
        List<EscuelaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Escuelas", lista)
        );
    }

    @GetMapping("/buscar/{idEscuela}")
    public ResponseEntity<GenericObjectResponse<EscuelaDetalleResponse>> findById(@PathVariable("idEscuela") Integer id) throws Exception {
        Escuela obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Escuela encontrada", convertToResponseDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<EscuelaDetalleResponse>> registrar(@Valid @RequestBody EscuelaCreateRequest request) {
        GenericObjectResponse<EscuelaDetalleResponse> response = serviceImpl.registrarEscuela(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<EscuelaDetalleResponse>> saveAll(@Valid @RequestBody List<EscuelaCreateRequest> request) {
        GenericReponse<EscuelaDetalleResponse> response = serviceImpl.registrarEscuelaMultiples(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idEscuela}")
    public ResponseEntity<GenericObjectResponse<EscuelaDetalleResponse>> actualizar(
            @PathVariable("idEscuela") Integer id,
            @Valid @RequestBody EscuelaUpdateRequest dto) {
        GenericObjectResponse<EscuelaDetalleResponse> response = serviceImpl.actualizarEscuela(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private EscuelaDetalleResponse convertToResponseDTO(Escuela obj) {
        return modelMapper.map(obj, EscuelaDetalleResponse.class);
    }

    private Escuela convertToEntity(EscuelaCreateRequest dto) {
        return modelMapper.map(dto, Escuela.class);
    }
}