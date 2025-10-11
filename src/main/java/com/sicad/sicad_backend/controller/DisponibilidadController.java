package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.impl.DisponibilidadServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {
    private final IDisponibilidadService service;
    private final DisponibilidadServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DisponibilidadDetalleResponse>> findAll() throws Exception {
        List<DisponibilidadDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalleDTO)
                .toList();

        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Disponibilidads", lista)
        );
    }
    @GetMapping("/buscar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
        findById(@PathVariable("idDisponibilidad") Integer id) throws Exception {
        Disponibilidad obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Disponibilidad encontrada", convertToDetalleDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>> registrar(
            @Valid @RequestBody DisponibilidadCreateRequest request) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response =
                serviceImpl.registrarDisponibilidad(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DisponibilidadDetalleResponse>> saveAll(@Valid @RequestBody List<DisponibilidadCreateRequest> requests){
        BaseListReponse<DisponibilidadDetalleResponse> response = serviceImpl.registrarVariosDisponiblidadAll(requests);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DisponibilidadUpdateRequest request) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response =
                serviceImpl.actualizarDisponibilidad(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idDisponibilidad") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarDisponibilidad(id);
        return ResponseEntity.status(response.status()).body(response);
    }


    @GetMapping("/listar/{idDocente}/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<List<DisponibilidadResumenResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente,
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        BaseObjectResponse<List<DisponibilidadResumenResponse>> response = serviceImpl.listarDisponibilidadDocente(idDocente, idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }
    private DisponibilidadDetalleResponse convertToDetalleDTO(Disponibilidad obj) {
        return modelMapper.map(obj, DisponibilidadDetalleResponse.class);
    }
    private Disponibilidad convertToEntity(DisponibilidadCreateRequest dto) {
        return modelMapper.map(dto, Disponibilidad.class);
    }
}