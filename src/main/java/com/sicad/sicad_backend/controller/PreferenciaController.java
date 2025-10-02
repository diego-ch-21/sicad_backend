package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.service.impl.PreferenciaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferencia")
@RequiredArgsConstructor
public class PreferenciaController {
    private final IPreferenciaService service;
    private final ModelMapper modelMapper;
    private final PreferenciaServiceImpl serviceImpl;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<PreferenciaDetalleResponse>> findAll() throws Exception {
        List<PreferenciaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Preferencias", lista)
        );
    }
    @GetMapping("/buscar/{idPreferencia}")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> findById(@PathVariable("idPreferencia") Integer id) throws Exception {
        Preferencia obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Preferencia encontrada", convertToDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> save(@Valid @RequestBody PreferenciaCreateRequest request){
        GenericObjectResponse<PreferenciaDetalleResponse> response = serviceImpl.registrarPreferencia(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<PreferenciaDetalleResponse>> saveAll(@Valid @RequestBody List<PreferenciaCreateRequest> requests){
        GenericReponse<PreferenciaDetalleResponse> response = serviceImpl.registrarVariosPreferencias(requests);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idPreferencia}")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> update(@PathVariable("idPreferencia") Integer id, @Valid @RequestBody PreferenciaUpdateRequest request) throws Exception {
        GenericObjectResponse<PreferenciaDetalleResponse> response = serviceImpl.actualizarPreferencia(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idPreferencia}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idPreferencia") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarPreferencia(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idDocente}/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<List<PreferenciaResumenResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idDocente") Integer idDocente,
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        GenericObjectResponse<List<PreferenciaResumenResponse>>  response = serviceImpl.listarPreferenciaDocente(idDocente, idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }
    private PreferenciaDetalleResponse convertToDTO(Preferencia obj) {
        return modelMapper.map(obj, PreferenciaDetalleResponse.class);
    }
}
