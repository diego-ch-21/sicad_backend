package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.service.impl.DedicacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dedicacion")
@RequiredArgsConstructor
public class DedicacionController {

    private final IDedicacionService service;
    private final DedicacionServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>> findAll() throws Exception {
        List<DedicacionDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Dedicaciones", lista)
        );
    }

    @GetMapping("/buscar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>> findById(@PathVariable("idDedicacion") Integer id) throws Exception {
        Dedicacion obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Dedicación encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>> save(@Valid @RequestBody DedicacionCreateRequest dto) throws Exception {

        Dedicacion obj = convertToEntity(dto);
        obj.setEnabled(true);
        service.save(obj);
        return new ResponseEntity<>(
                new BaseListReponse<>(201, "Dedicación creada", List.of(convertToDetalle(obj))),
                HttpStatus.CREATED
        );
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>> saveAll(@Valid @RequestBody List<DedicacionCreateRequest> lista) throws Exception {
        BaseListReponse<DedicacionDetalleResponse> response = serviceImpl.saveAll(lista);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{idDedicacion}")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>>
        update(@PathVariable("idDedicacion") Integer id, @Valid @RequestBody DedicacionCreateRequest dto) throws Exception {
        Dedicacion obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Dedicación actualizada", List.of(convertToDetalle(obj)))
        );
    }

    @DeleteMapping("/eliminar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idDedicacion") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarDedicacion(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>> buscarDedicacionSegunDocente(@PathVariable("idDocente") Integer id) throws Exception {
        BaseObjectResponse<DedicacionDetalleResponse> response = serviceImpl.obtenerDedicacion(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    private DedicacionCreateRequest convertToDTO(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionCreateRequest.class);
    }

    private DedicacionDetalleResponse convertToDetalle(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionDetalleResponse.class);
    }

    private Dedicacion convertToEntity(DedicacionCreateRequest dto) {
        return modelMapper.map(dto, Dedicacion.class);
    }
}
