package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResponseDTO;
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
    public ResponseEntity<GenericReponse<DocenteRequestDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Docente obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Docente encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DocenteRequestDTO>> save(@Valid @RequestBody DocenteRequestDTO dto) throws Exception {
        Docente obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Docente creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DocenteRequestDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody DocenteRequestDTO dto) throws Exception {
        Docente obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Docente actualizada", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/registrar")
    public ResponseEntity<GenericObjectResponse<DocenteResponseDTO>> registrarDocente(@RequestBody DocenteRequestDTO request) {
        GenericObjectResponse<DocenteResponseDTO> response = serviceImpl.registrarDocente(request);
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