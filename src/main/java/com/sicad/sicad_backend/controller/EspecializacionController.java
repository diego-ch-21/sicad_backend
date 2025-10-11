package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUdpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
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
    public ResponseEntity<BaseListReponse<EspecializacionDetalleResponse>> listarEspecializacion(){
        List<EspecializacionDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200,"listar especialistas",lista)
        );
    }
    @GetMapping("/buscar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
        findById(@PathVariable("idEspecializacion") Integer id) throws Exception {
        Especializacion obj = service.findById(id);
        if(obj == null){
            return ResponseEntity.ok(
                    new BaseObjectResponse<>(200, "Asignatura no encontrada", convertToDetalle(obj))
            );        }
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Asignatura encontrada", convertToDetalle(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>> registrar(@Valid @RequestBody EspecializacionCreateRequest dto) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = serviceImpl.registrarEspecializacion(dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<EspecializacionDetalleResponse>> registrarAll(@Valid @RequestBody List<EspecializacionCreateRequest> dto) {
        BaseListReponse<EspecializacionDetalleResponse> response = serviceImpl.registrarAllEspecializacion(dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
        actualizar(@PathVariable("idEspecializacion") Integer id, @Valid @RequestBody EspecializacionUdpdateRequest dto) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = serviceImpl.actualizarEspecialidad(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idEspecializacion") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarEspecializacion(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idDocente}")
    public ResponseEntity<BaseObjectResponse<List<EspecializacionResumenResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente) throws Exception {
        BaseObjectResponse<List<EspecializacionResumenResponse>> response = serviceImpl.listarEspecializacionDocente(idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }
    private EspecializacionDetalleResponse convertToDetalle(Especializacion obj){
        return modelMapper.map(obj, EspecializacionDetalleResponse.class);
    }
}
