package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.docente.DocenteDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteUpdateRequest;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.dto.docente.DocenteCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.DocenteServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {
    private final IDocenteService service;
    private final DocenteServiceImpl serviceImpl;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DocenteDetalleResponse>> findAll() throws Exception {
        List<DocenteDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Docentes", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>>  findById(@PathVariable("id") Integer id) throws Exception {
        Docente obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Docente encontrada", convertToResponseDTO(obj))
        );
    }
    @GetMapping("/usuario/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>>  findByIdUsuario(@PathVariable("id") Integer id) throws Exception {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.obtenerDocentePorUsuario(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>> registrarDocente(@Valid @RequestBody DocenteCreateRequest request) {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.registrarDocente(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<DocenteDetalleResponse>> registrarDocentes(@Valid @RequestBody List<DocenteCreateRequest> requestAll) {
        GenericReponse<DocenteDetalleResponse> response = serviceImpl.registrarDocentes(requestAll);
        return ResponseEntity.status(response.status()).body(response);
    }


    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>> update(@Valid @PathVariable("id") Integer id, @Valid @RequestBody DocenteUpdateRequest dto) throws Exception {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.actualizarDocente(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private DocenteDetalleResponse convertToResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDetalleResponse.class);
    }
    private Docente convertToEntity(DocenteCreateRequest dto) {
        return modelMapper.map(dto, Docente.class);
    }
}