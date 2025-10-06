package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.Categoria;
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
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> findAll() throws Exception {
        List<CursoDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de asignaturas", lista)
        );
    }
    @GetMapping("/buscar/{idCurso}")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>> findById(@PathVariable("idCurso") Integer id) throws Exception {
        Curso curso = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Curso encontrado", convertToDetalle(curso))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>> save(@Valid @RequestBody CursoCreateRequest request) {
        GenericObjectResponse<CursoDetalleResponse> response = serviceImpl.registrarCurso(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> saveAll(@Valid @RequestBody List<CursoCreateRequest> request) {
        GenericReponse<CursoDetalleResponse> response = serviceImpl.registrarCursosMultiples(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idCurso}")
    public ResponseEntity<GenericObjectResponse<CursoDetalleResponse>>
            update(@PathVariable("idCurso") Integer id, @Valid @RequestBody CursoUpdateRequest request) throws Exception {
        GenericObjectResponse<CursoDetalleResponse> response  = serviceImpl.actualizarCurso(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idCurso}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idCurso") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarCurso(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<CursoDetalleResponse>> findByDocenteAndCargaElectiva(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        GenericReponse<CursoDetalleResponse> response = serviceImpl.listarCursosPorCicloAcademico(idCicloAcademico);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/horario/insertar")
    public ResponseEntity<GenericObjectResponse<HorarioDetalleResponse> > saveHorario(@PathVariable("idCurso") Integer id,@Valid @RequestBody HorarioCreateRequest request) {
        GenericObjectResponse<HorarioDetalleResponse>  response = serviceImpl.registrarCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/horario/insertar-all")
    public ResponseEntity<GenericReponse<HorarioDetalleResponse> > saveHorarioAll(@PathVariable("idCurso") Integer id,@Valid @RequestBody List<HorarioCreateRequest> request) {
        GenericReponse<HorarioDetalleResponse>   response = serviceImpl.registrarVariosCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/horario/actualizar/{idCursoHorario}")
    public ResponseEntity<GenericObjectResponse<HorarioDetalleResponse>>
    actualizarHorario(@PathVariable("idCursoHorario") Integer id,@Valid @RequestBody HorarioUpdateRequest request) {
        GenericObjectResponse<HorarioDetalleResponse>  response = serviceImpl.actualizarCursoHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/horario/eliminar/{idCursoHorario}")
    public ResponseEntity<GenericObjectResponse<String>> deleteHorario(@PathVariable("idCursoHorario") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarCursoHorario(id);
        return ResponseEntity.status(response.status()).body(response);
    }



    private CursoDetalleResponse convertToDetalle(Curso curso) {
        return modelMapper.map(curso, CursoDetalleResponse.class);
    }
}