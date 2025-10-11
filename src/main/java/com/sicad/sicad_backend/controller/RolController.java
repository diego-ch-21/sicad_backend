package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.dto.RolDTO;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
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
    public ResponseEntity<BaseListReponse<RolDTO>> findAll() throws Exception {
        List<RolDTO> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Rols", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<BaseObjectResponse<RolDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Rol obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Rol encontrada", convertToDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<BaseListReponse<RolDTO>> save(@Valid @RequestBody RolDTO dto) throws Exception {
        Rol obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new BaseListReponse<>(
                201, "Rol creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<BaseListReponse<RolDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody RolDTO dto) throws Exception {
        Rol obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Rol actualizada", List.of(convertToDTO(obj)))
        );
    }

    private RolDTO convertToDTO(Rol obj) {
        return modelMapper.map(obj, RolDTO.class);
    }
    private Rol convertToEntity(RolDTO dto) {
        return modelMapper.map(dto, Rol.class);
    }
}