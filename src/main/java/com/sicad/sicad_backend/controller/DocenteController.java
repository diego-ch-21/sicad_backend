package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResponseDTO;
import com.sicad.sicad_backend.dto.docente.DocenteUpdateRequestDTO;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.dto.docente.DocenteRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.DocenteServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<GenericReponse<DocenteResponseDTO>> findAll() throws Exception {
        List<DocenteResponseDTO> lista = service.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Docentes", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteResponseDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Docente obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Docente encontrada", convertToResponseDTO(obj))
        );
    }
    @GetMapping("/usuario/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteResponseDTO>>  findByIdUsuario(@PathVariable("id") Integer id) throws Exception {
        GenericObjectResponse<DocenteResponseDTO> response = serviceImpl.obtenerDocentePorUsuario(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/registrar")
    public ResponseEntity<GenericObjectResponse<DocenteResponseDTO>> registrarDocente(@Valid @RequestBody DocenteRequestDTO request) {
        GenericObjectResponse<DocenteResponseDTO> response = serviceImpl.registrarDocente(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<DocenteResponseDTO>> update(@Valid @PathVariable("id") Integer id,@Valid @RequestBody DocenteUpdateRequestDTO dto) throws Exception {
        GenericObjectResponse<DocenteResponseDTO> response = serviceImpl.actualizarDocente(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private DocenteRequestDTO convertToDTO(Docente obj) {
        return modelMapper.map(obj, DocenteRequestDTO.class);
    }
    private DocenteResponseDTO convertToResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteResponseDTO.class);
    }
    private Docente convertToEntity(DocenteRequestDTO dto) {
        return modelMapper.map(dto, Docente.class);
    }
}