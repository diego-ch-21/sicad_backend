package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.DocenteServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {
    private final IDocenteService service;
    private final DocenteServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DocenteDetalleResponse>> findAll() throws Exception {
        List<DocenteDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Docentes", lista)
        );
    }
    @GetMapping("/buscar/{idDocente}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>>  findById(@PathVariable("idDocente") Integer id) throws Exception {
        Docente obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Docente encontrada", convertToResponseDTO(obj))
        );
    }
    @GetMapping("/usuario/buscar/{idUsuario}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>>  findByIdUsuario(@PathVariable("idUsuario") Integer id) throws Exception {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.obtenerDocentePorUsuario(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>> registrarDocente(@Valid @RequestBody DocenteCreateRequest request) {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.registrarDocente(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<DocenteDetalleResponse>> registrarDocentes(@Valid @RequestBody List<DocenteCreateRequest> requestAll) {
        GenericReponse<DocenteDetalleResponse> response = serviceImpl.registrarDocentes(requestAll);
        return ResponseEntity.status(response.status()).body(response);
    }


    @PutMapping("/actualizar/{idDocente}")
    public ResponseEntity<GenericObjectResponse<DocenteDetalleResponse>> update(@Valid @PathVariable("id") Integer id, @Valid @RequestBody DocenteUpdateRequest dto) throws Exception {
        GenericObjectResponse<DocenteDetalleResponse> response = serviceImpl.actualizarDocente(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idDocente}")
    public ResponseEntity<GenericObjectResponse<String>>
            delete(@PathVariable("idDocente") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarDocente(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/especializaciones")
    public ResponseEntity<GenericReponse<DocenteEspecializacionResponse>> docenteEspecializacion() throws Exception {
        GenericReponse<DocenteEspecializacionResponse> response = serviceImpl.listarDocentesConEspecializaciones();
        return  ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/preferencias/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<DocentePreferenciaResponse>>
        docentesPreferencias(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        GenericReponse<DocentePreferenciaResponse> response = serviceImpl.listarDocentesConPreferencias(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/disponibilidades/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<DocenteDisponibilidadResponse>>
        docentesDisponibilidad(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        GenericReponse<DocenteDisponibilidadResponse> response = serviceImpl.listarDocentesConDisponibilidad(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/asignaciones/{idCarga}")
    public ResponseEntity<GenericReponse<DocenteAsignacionResponse>>
        docentesAsignacionesCicloAcademicoAndCarga(
            @PathVariable("idCarga") Integer idCarga) throws Exception {
        GenericReponse<DocenteAsignacionResponse> response = serviceImpl.listarDocentesCargaConAsignaciones(idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    private DocenteDetalleResponse convertToResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDetalleResponse.class);
    }

    private Docente convertToEntity(DocenteCreateRequest dto) {
        return modelMapper.map(dto, Docente.class);
    }
}