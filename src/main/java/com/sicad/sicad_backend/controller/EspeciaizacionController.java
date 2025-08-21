package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUdpdateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.service.impl.EspecializacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especializacion")
@RequiredArgsConstructor
public class EspeciaizacionController {
    private final IEspecializacionService service;
    private final EspecializacionServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<EspecializacionDetalleResponse>> listarEspecializacion(){
        List<EspecializacionDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200,"listar especialistas",lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<EspecializacionDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Especializacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Asignatura encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<EspecializacionDetalleResponse>> registrar(@Valid @RequestBody EspecializacionCreateRequest dto) {
        GenericObjectResponse<EspecializacionDetalleResponse> response = serviceImpl.registrarEspecializacion(dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<EspecializacionDetalleResponse>> registrarAll(@Valid @RequestBody List<EspecializacionCreateRequest> dto) {
        GenericReponse<EspecializacionDetalleResponse> response = serviceImpl.registrarAllEspecializacion(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<EspecializacionDetalleResponse>> actualizar(@PathVariable("id") Integer id, @Valid @RequestBody EspecializacionUdpdateRequest dto) {
        GenericObjectResponse<EspecializacionDetalleResponse> response = serviceImpl.actualizarEspecialidad(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private EspecializacionDetalleResponse convertToDetalle(Especializacion obj){
        return modelMapper.map(obj, EspecializacionDetalleResponse.class);
    }
}
