package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
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
    private final CategoriaServiceImpl serviceImpl;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>> findAll() throws Exception {
        List<CategoriaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Categorías", lista)
        );
    }

    @GetMapping("/buscar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>> findById(@PathVariable("idCategoria") Integer id) throws Exception {
        Categoria obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Categoría encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>> save(@Valid @RequestBody CategoriaCreateRequest dto) throws Exception {
        Categoria obj = convertToEntity(dto);
        obj.setEnabled(true);
        service.save(obj);
        return new ResponseEntity<>(
                new BaseListReponse<>(201, "Categoría creada", List.of(convertToDetalle(obj))),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>> saveAll(@Valid @RequestBody List<CategoriaCreateRequest> lista) throws Exception {
        BaseListReponse<CategoriaDetalleResponse> response = serviceImpl.saveAll(lista);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{idCategoria}")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>> update(@PathVariable("idCategoria") Integer id, @Valid @RequestBody CategoriaCreateRequest dto) throws Exception {
        Categoria obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Categoría actualizada", List.of(convertToDetalle(obj)))
        );
    }

    @DeleteMapping("/eliminar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idCategoria") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarCategoria(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>> buscarCategoriaSegunDocente(@PathVariable("idDocente") Integer id) throws Exception {
        BaseObjectResponse<CategoriaDetalleResponse> response = serviceImpl.obtenerCategoria(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    private CategoriaDetalleResponse convertToDetalle(Categoria obj) {
        return modelMapper.map(obj, CategoriaDetalleResponse.class);
    }

    private Categoria convertToEntity(CategoriaCreateRequest dto) {
        return modelMapper.map(dto, Categoria.class);
    }
}