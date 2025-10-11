package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
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
    public ResponseEntity<BaseListReponse<UsuarioDTO>> findAll() throws Exception {
        List<UsuarioDTO> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Usuarios", lista)
        );
    }
    //para el login
    @GetMapping("/buscar/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<UsuarioDTO>>  findById(@PathVariable("idUsuario") Integer id) throws Exception {
        Usuario obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Usuario encontrada", convertToDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<BaseListReponse<UsuarioDTO>> save(@Valid @RequestBody UsuarioDTO dto) throws Exception {
        Usuario obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new BaseListReponse<>(
                201, "Usuario creada exitosamente", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    //para actualizar
    @PutMapping("/actualizar/{idUsuario}")
    public ResponseEntity<BaseListReponse<UsuarioDTO>> update(@Valid @PathVariable("idUsuario") Integer id, @RequestBody UsuarioDTO dto) throws Exception {
        Usuario obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Usuario actualizada", List.of(convertToDTO(obj)))
        );
    }

    private UsuarioDTO convertToDTO(Usuario obj) {
        return modelMapper.map(obj, UsuarioDTO.class);
    }
    private Usuario convertToEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, Usuario.class);
    }
}