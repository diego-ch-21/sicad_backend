package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.service.impl.DirectorServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    private final IDirectorService service;
    private final DirectorServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DirectorDetalleResponse>> findAll() throws Exception {
        List<DirectorDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Directores", lista)
        );
    }

    @GetMapping("/buscar/{idDirector}")
    public ResponseEntity<GenericObjectResponse<DirectorDetalleResponse>> findById(@PathVariable("idDirector") Integer id) throws Exception {
        Director obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Director encontrado",convertToResponseDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<DirectorDetalleResponse>> registrarDocente(@Valid @RequestBody DirectorCreateRequest request) {
        GenericObjectResponse<DirectorDetalleResponse> response = serviceImpl.registrarDirector(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idDirector}")
    public ResponseEntity<GenericObjectResponse<DirectorDetalleResponse>>
        update(@Valid @PathVariable("idDirector") Integer id, @Valid @RequestBody DirectorUpdateRequest dto) throws Exception {
        GenericObjectResponse<DirectorDetalleResponse> response = serviceImpl.actualizarDirector(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idDirector}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idDirector") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarDirector(id);
        return ResponseEntity.status(response.status()).body(response);
    }


    private DirectorCreateRequest convertToDTO(Director obj) {
        return modelMapper.map(obj, DirectorCreateRequest.class);
    }
    private DirectorDetalleResponse convertToResponseDTO(Director obj) {
        return modelMapper.map(obj, DirectorDetalleResponse.class);
    }

    private Director convertToEntity(DirectorCreateRequest dto) {
        return modelMapper.map(dto, Director.class);
    }
}
