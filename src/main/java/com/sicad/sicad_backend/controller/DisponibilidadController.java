package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {
    private final IDisponibilidadService service;


    @GetMapping("/listar/{idDocente}/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<DisponibilidadResumenResponse>>
            listar(@PathVariable("idDocente") Integer idDocente,
                   @PathVariable("idCicloAcademico") Integer idCicloAcademico) {
        BaseListReponse<DisponibilidadResumenResponse> response = service.listarPorDocenteCicloAcademico(idDocente,idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
            buscar(@PathVariable("idDisponibilidad") Integer id) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
            registrar(@Valid @RequestBody DisponibilidadCreateRequest request) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DisponibilidadDetalleResponse>>
            registrarAll(@Valid @RequestBody List<DisponibilidadCreateRequest> request) {
        BaseListReponse<DisponibilidadDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
            actualizar(@PathVariable("idDisponibilidad") Integer id, @Valid @RequestBody DisponibilidadUpdateRequest dto) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idDisponibilidad") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}