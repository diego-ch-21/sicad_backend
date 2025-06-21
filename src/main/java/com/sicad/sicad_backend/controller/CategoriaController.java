package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.CategoriaDTO;
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
    public ResponseEntity<GenericReponse<CategoriaDTO>> findAll() throws Exception {
        List<CategoriaDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Categorías", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<CategoriaDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Categoria obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Categoría encontrada", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<CategoriaDTO>> save(@Valid @RequestBody CategoriaDTO dto) throws Exception {
        Categoria obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Categoría creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CategoriaDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody CategoriaDTO dto) throws Exception {
        Categoria obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Categoría actualizada", List.of(convertToDTO(obj)))
        );
    }


    private CategoriaDTO convertToDTO(Categoria obj) {
        return modelMapper.map(obj, CategoriaDTO.class);
    }

    private Categoria convertToEntity(CategoriaDTO dto) {
        return modelMapper.map(dto, Categoria.class);
    }
}