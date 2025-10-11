package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.service.impl.CursoServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService service;
    private final CursoServiceImpl serviceImpl;
    private final ModelMapper modelMapper;


    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>> findAll() throws Exception {
        List<CursoDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de asignaturas", lista)
        );
    }
    @GetMapping("/buscar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>> findById(@PathVariable("idCurso") Integer id) throws Exception {
        Curso curso = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Curso encontrado", convertToDetalle(curso))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>> save(@Valid @RequestBody CursoCreateRequest request) {
        BaseObjectResponse<CursoDetalleResponse> response = service.registrarCurso(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>> saveAll(@Valid @RequestBody List<CursoCreateRequest> request) {
        BaseListReponse<CursoDetalleResponse> response = service.registrarCursosMultiples(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
            update(@PathVariable("idCurso") Integer id, @Valid @RequestBody CursoUpdateRequest request) throws Exception {
        BaseObjectResponse<CursoDetalleResponse> response  = service.actualizarCurso(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idCurso") Integer id) {
        BaseObjectResponse<String> response = service.eliminarCurso(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>> findByDocenteAndCargaElectiva(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        BaseListReponse<CursoDetalleResponse> response = service.listarCursosPorCicloAcademico(idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    //----------------------------------------------------------
    @PostMapping("/{idCurso}/horario/insertar")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>> saveHorario(@PathVariable("idCurso") Integer id, @Valid @RequestBody HorarioCreateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = serviceImpl.registrarCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/horario/insertar-all")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>> saveHorarioAll(@PathVariable("idCurso") Integer id, @Valid @RequestBody List<HorarioCreateRequest> request) {
        BaseListReponse<HorarioDetalleResponse> response = serviceImpl.registrarVariosCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/horario/actualizar/{idCursoHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
    actualizarHorario(@PathVariable("idCursoHorario") Integer id,@Valid @RequestBody HorarioUpdateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = serviceImpl.actualizarCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/horario/eliminar/{idCursoHorario}")
    public ResponseEntity<BaseObjectResponse<String>> deleteHorario(@PathVariable("idCursoHorario") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarCursoHorario(id);
        return ResponseEntity.status(response.status()).body(response);
    }



    private CursoDetalleResponse convertToDetalle(Curso curso) {
        return modelMapper.map(curso, CursoDetalleResponse.class);
    }
}