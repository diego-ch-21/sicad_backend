package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.DisponibilidadServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {
    private final IDisponibilidadService service;
    private final DisponibilidadServiceImpl disponibilidadServiceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DisponibilidadDetalleResponse>> findAll() throws Exception {
        List<DisponibilidadDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalleDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Disponibilidads", lista)
        );
    }
    @GetMapping("/buscar/{idDisponibilidad}")
    public ResponseEntity<GenericObjectResponse<DisponibilidadDetalleResponse>>
        findById(@PathVariable("idDisponibilidad") Integer id) throws Exception {
        Disponibilidad obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Disponibilidad encontrada", convertToDetalleDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<DisponibilidadDetalleResponse>> registrar(
            @Valid @RequestBody DisponibilidadCreateRequest request) {
        GenericObjectResponse<DisponibilidadDetalleResponse> response =
                disponibilidadServiceImpl.registrarDisponibilidad(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<DisponibilidadDetalleResponse>> saveAll(@Valid @RequestBody List<DisponibilidadCreateRequest> requests){
        GenericReponse<DisponibilidadDetalleResponse> response = disponibilidadServiceImpl.registrarVariosDisponiblidadAll(requests);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idDisponibilidad}")
    public ResponseEntity<GenericObjectResponse<DisponibilidadDetalleResponse>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DisponibilidadUpdateRequest request) {
        GenericObjectResponse<DisponibilidadDetalleResponse> response =
                disponibilidadServiceImpl.actualizarDisponibilidad(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idDocente}/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<List<DisponibilidadResumenResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente,
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        GenericObjectResponse<List<DisponibilidadResumenResponse>>  response = disponibilidadServiceImpl.listarDisponibilidadDocente(idDocente, idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }
    private DisponibilidadDetalleResponse convertToDetalleDTO(Disponibilidad obj) {
        return modelMapper.map(obj, DisponibilidadDetalleResponse.class);
    }
    private Disponibilidad convertToEntity(DisponibilidadCreateRequest dto) {
        return modelMapper.map(dto, Disponibilidad.class);
    }
}