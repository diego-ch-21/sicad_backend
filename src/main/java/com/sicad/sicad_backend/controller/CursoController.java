package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.CursoHorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
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
    private final CursoServiceImpl cursoServiceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> findAll() throws Exception {
        GenericReponse<CursoDetalleResponse> response = cursoServiceImpl.listarCursosConHorarios();
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/listar/{idPeriodoAcademico}")
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> findByDocenteAndCargaElectiva(
            @PathVariable("idPeriodoAcademico") Integer idPeriodoAcademico) {
        GenericReponse<CursoDetalleResponse> response = cursoServiceImpl.listarCursosPorCicloAcademico(idPeriodoAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Curso curso = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Curso encontrado", convertToDetalle(curso))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>> save(@Valid @RequestBody CursoCreateRequest request) {
        GenericObjectResponse<CursoDetalleResponse> response = cursoServiceImpl.registrarCurso(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> saveAll(@Valid @RequestBody List<CursoCreateRequest> request) {
        GenericReponse<CursoDetalleResponse> response = cursoServiceImpl.registrarCursosMultiples(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/horario/insertar")
    public ResponseEntity<GenericObjectResponse<HorarioDetalleResponse> > saveHorario(@PathVariable("idCurso") Integer id,@Valid @RequestBody HorarioCreateRequest request) {
        GenericObjectResponse<HorarioDetalleResponse>  response = cursoServiceImpl.registrarCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/horario/insertar-all")
    public ResponseEntity<GenericReponse<HorarioDetalleResponse> > saveHorarioAll(@PathVariable("idCurso") Integer id,@Valid @RequestBody List<HorarioCreateRequest> request) {
        GenericReponse<HorarioDetalleResponse>   response = cursoServiceImpl.registrarVariosCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }


    private CursoDetalleResponse convertToDetalle(Curso curso) {
        return modelMapper.map(curso, CursoDetalleResponse.class);
    }
}