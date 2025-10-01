package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUdpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.service.impl.EspecializacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especializacion")
@RequiredArgsConstructor
public class EspecializacionController {
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
    @GetMapping("/buscar/{idEspecializacion}")
    public ResponseEntity<GenericObjectResponse<EspecializacionDetalleResponse>>
        findById(@PathVariable("idEspecializacion") Integer id) throws Exception {
        Especializacion obj = service.findById(id);
        if(obj == null){
            return ResponseEntity.ok(
                    new GenericObjectResponse<>(200, "Asignatura no encontrada", convertToDetalle(obj))
            );        }
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
    @PutMapping("/actualizar/{idEspecializacion}")
    public ResponseEntity<GenericObjectResponse<EspecializacionDetalleResponse>>
        actualizar(@PathVariable("idEspecializacion") Integer id, @Valid @RequestBody EspecializacionUdpdateRequest dto) {
        GenericObjectResponse<EspecializacionDetalleResponse> response = serviceImpl.actualizarEspecialidad(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/listar/{idDocente}")
    public ResponseEntity<GenericObjectResponse<List<EspecializacionResumenResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente) throws Exception {
        GenericObjectResponse<List<EspecializacionResumenResponse>>  response = serviceImpl.listarEspecializacionDocente(idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }
    private EspecializacionDetalleResponse convertToDetalle(Especializacion obj){
        return modelMapper.map(obj, EspecializacionDetalleResponse.class);
    }
}
