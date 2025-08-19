package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaResumenResponse;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.service.impl.CategoriaServiceImpl;
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
    private final CategoriaServiceImpl categoriaService;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CategoriaDetalleResponse>> findAll() throws Exception {
        List<CategoriaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Categorías", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<CategoriaDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Categoria obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Categoría encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericReponse<CategoriaDetalleResponse>> save(@Valid @RequestBody CategoriaCreateRequest dto) throws Exception {
        Categoria obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Categoría creada", List.of(convertToDetalle(obj))),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<CategoriaDetalleResponse>> saveAll(@Valid @RequestBody List<CategoriaCreateRequest> lista) throws Exception {
        GenericReponse<CategoriaDetalleResponse> response = categoriaService.saveAll(lista);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CategoriaDetalleResponse>> update(@PathVariable("id") Integer id, @Valid @RequestBody CategoriaCreateRequest dto) throws Exception {
        Categoria obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Categoría actualizada", List.of(convertToDetalle(obj)))
        );
    }


    private CategoriaDetalleResponse convertToDetalle(Categoria obj) {
        return modelMapper.map(obj, CategoriaDetalleResponse.class);
    }

    private Categoria convertToEntity(CategoriaCreateRequest dto) {
        return modelMapper.map(dto, Categoria.class);
    }
}