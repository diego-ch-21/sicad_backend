package com.sicad.sicad_backend.presentation.controller;


import com.sicad.sicad_backend.persistence.model.Rol;
import com.sicad.sicad_backend.presentation.dto.RolDTO;
import com.sicad.sicad_backend.presentation.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IRolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rol")
@RequiredArgsConstructor
public class RolController {
    private final IRolService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<RolDTO>> findAll() throws Exception {
        List<RolDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Rols", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<RolDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Rol obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Rol encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<RolDTO>> save(@Valid @RequestBody RolDTO dto) throws Exception {
        Rol obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Rol creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<RolDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody RolDTO dto) throws Exception {
        Rol obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Rol actualizada", List.of(convertToDTO(obj)))
        );
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RolDTO convertToDTO(Rol obj) {
        return modelMapper.map(obj, RolDTO.class);
    }
    private Rol convertToEntity(RolDTO dto) {
        return modelMapper.map(dto, Rol.class);
    }
}