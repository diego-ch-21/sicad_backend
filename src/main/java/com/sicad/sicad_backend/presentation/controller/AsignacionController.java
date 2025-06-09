package com.sicad.sicad_backend.presentation.controller;


import com.sicad.sicad_backend.persistence.model.Asignacion;
import com.sicad.sicad_backend.presentation.dto.AsignacionDTO;
import com.sicad.sicad_backend.presentation.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignacion")
@RequiredArgsConstructor
public class AsignacionController {
    private final IAsignacionService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AsignacionDTO>> findAll() throws Exception {
        List<AsignacionDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Asignacions", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<AsignacionDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Asignacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Asignacion encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<AsignacionDTO>> save(@Valid @RequestBody AsignacionDTO dto) throws Exception {
        Asignacion obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Asignacion creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<AsignacionDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody AsignacionDTO dto) throws Exception {
        Asignacion obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Asignacion actualizada", List.of(convertToDTO(obj)))
        );
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AsignacionDTO convertToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDTO.class);
    }
    private Asignacion convertToEntity(AsignacionDTO dto) {
        return modelMapper.map(dto, Asignacion.class);
    }
}