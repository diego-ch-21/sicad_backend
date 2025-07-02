package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoPeriodoAcademicoResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.service.impl.CursoServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
        List<CursoDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDetalle)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Cursos", lista)
        );
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

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>> update(@PathVariable("id") Integer id,
                                                                              @Valid @RequestBody CursoUpdateRequest request) {
        GenericObjectResponse<CursoDetalleResponse> response = cursoServiceImpl.actualizarCurso(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idPeriodoAcademico}")
    public ResponseEntity<GenericObjectResponse<List<CursoPeriodoAcademicoResponse>>> findByDocenteAndCargaElectiva(
            @PathVariable("idPeriodoAcademico") Integer idPeriodoAcademico) {
        GenericObjectResponse<List<CursoPeriodoAcademicoResponse>>  response = cursoServiceImpl.listarCursoPeriodoAcademico(idPeriodoAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    private CursoDetalleResponse convertToDetalle(Curso curso) {
        return modelMapper.map(curso, CursoDetalleResponse.class);
    }
}