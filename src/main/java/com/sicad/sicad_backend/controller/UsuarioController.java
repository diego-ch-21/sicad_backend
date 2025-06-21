package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.dto.UsuarioDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final IUsuarioService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<UsuarioDTO>> findAll() throws Exception {
        List<UsuarioDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Usuarios", lista)
        );
    }
    //para el login
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<UsuarioDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Usuario obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Usuario encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<UsuarioDTO>> save(@Valid @RequestBody UsuarioDTO dto) throws Exception {
        Usuario obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Usuario creada exitosamente", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    //para actualizar
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<UsuarioDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody UsuarioDTO dto) throws Exception {
        Usuario obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Usuario actualizada", List.of(convertToDTO(obj)))
        );
    }

    private UsuarioDTO convertToDTO(Usuario obj) {
        return modelMapper.map(obj, UsuarioDTO.class);
    }
    private Usuario convertToEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, Usuario.class);
    }
}