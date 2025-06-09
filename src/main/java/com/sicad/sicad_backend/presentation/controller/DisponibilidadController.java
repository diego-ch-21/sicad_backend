package com.sicad.sicad_backend.presentation.controller;


import com.sicad.sicad_backend.persistence.model.Disponibilidad;
import com.sicad.sicad_backend.presentation.dto.DisponibilidadDTO;
import com.sicad.sicad_backend.presentation.dto.base.GenericReponse;
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
    public ResponseEntity<GenericReponse<DisponibilidadDTO>> findAll() throws Exception {
        List<DisponibilidadDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Disponibilidads", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DisponibilidadDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Disponibilidad obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Disponibilidad encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DisponibilidadDTO>> save(@Valid @RequestBody DisponibilidadDTO dto) throws Exception {
        Disponibilidad obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Disponibilidad creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DisponibilidadDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody DisponibilidadDTO dto) throws Exception {
        Disponibilidad obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Disponibilidad actualizada", List.of(convertToDTO(obj)))
        );
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private DisponibilidadDTO convertToDTO(Disponibilidad obj) {
        return modelMapper.map(obj, DisponibilidadDTO.class);
    }
    private Disponibilidad convertToEntity(DisponibilidadDTO dto) {
        return modelMapper.map(dto, Disponibilidad.class);
    }
}