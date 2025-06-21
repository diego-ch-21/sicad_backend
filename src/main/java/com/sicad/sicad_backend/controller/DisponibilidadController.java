package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {
    private final IDisponibilidadService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DisponibilidadRequestDTO>> findAll() throws Exception {
        List<DisponibilidadRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Disponibilidads", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DisponibilidadRequestDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Disponibilidad obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Disponibilidad encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DisponibilidadRequestDTO>> save(@Valid @RequestBody DisponibilidadRequestDTO dto) throws Exception {
        Disponibilidad obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Disponibilidad creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DisponibilidadRequestDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody DisponibilidadRequestDTO dto) throws Exception {
        Disponibilidad obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Disponibilidad actualizada", List.of(convertToDTO(obj)))
        );
    }

    private DisponibilidadRequestDTO convertToDTO(Disponibilidad obj) {
        return modelMapper.map(obj, DisponibilidadRequestDTO.class);
    }
    private Disponibilidad convertToEntity(DisponibilidadRequestDTO dto) {
        return modelMapper.map(dto, Disponibilidad.class);
    }
}