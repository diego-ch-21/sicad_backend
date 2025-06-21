package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final ICategoriaService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CategoriaRequestDTO>> findAll() throws Exception {
        List<CategoriaRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Categorías", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<CategoriaRequestDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Categoria obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Categoría encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericReponse<CategoriaRequestDTO>> save(@Valid @RequestBody CategoriaRequestDTO dto) throws Exception {
        Categoria obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Categoría creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CategoriaRequestDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody CategoriaRequestDTO dto) throws Exception {
        Categoria obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Categoría actualizada", List.of(convertToDTO(obj)))
        );
    }


    private CategoriaRequestDTO convertToDTO(Categoria obj) {
        return modelMapper.map(obj, CategoriaRequestDTO.class);
    }

    private Categoria convertToEntity(CategoriaRequestDTO dto) {
        return modelMapper.map(dto, Categoria.class);
    }
}