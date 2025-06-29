package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.model.CargaElectiva;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.service.interfaces.ICargaElectivaService;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/carga_electiva")
@RequiredArgsConstructor
public class CargaElectivaController {
    private final ICargaElectivaService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    ResponseEntity<GenericReponse<CargaElectivaDetalleResponse>> findAll() throws Exception {
        List<CargaElectivaDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Categorías", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<CargaElectivaDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        CargaElectiva obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Categoría encontrada", convertToDetalle(obj))
        );
    }
    private CargaElectivaDetalleResponse convertToDetalle(CargaElectiva obj) {
        return modelMapper.map(obj, CargaElectivaDetalleResponse.class);
    }

    private Categoria convertToEntity(CategoriaCreateRequest dto) {
        return modelMapper.map(dto, Categoria.class);
    }
}
