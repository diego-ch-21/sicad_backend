package com.sicad.sicad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/*
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CursoController.class)

 */
public class CursoControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICursoService service;

    @Autowired
    private ObjectMapper objectMapper;

    AsignaturaResumenResponse asignaturaMock = new AsignaturaResumenResponse(1, "Algoritmos","FDFD433");
    EscuelaResumenResponse escuelaMock = new EscuelaResumenResponse(1, "Facultad de Ingeniería");
    CicloAcademicoResumenResponse cicloMock = new CicloAcademicoResumenResponse(1, "2025-I",true);
    HorarioDetalleResponse horarioMock = new HorarioDetalleResponse(1, "T", "LUNES", "08:00:00", "10:00:00", 2, null,true);

    CursoDetalleResponse CURSO_1 = new CursoDetalleResponse(
            1, "CUR-001", "G1", 1, asignaturaMock,
            List.of("Ingeniería de Sistemas"), escuelaMock, cicloMock,
            List.of(horarioMock), true
    );

    CursoDetalleResponse CURSO_2 = new CursoDetalleResponse(
            2, "CUR-002", "G2", 2, asignaturaMock,
            List.of("Ingeniería Industrial"), escuelaMock, cicloMock,
            List.of(horarioMock), true
    );

    // --- LISTAR POR CICLO ACADÉMICO ---
    @Test
    void listarPorCicloAcademicoTest() throws Exception {
        BaseListReponse<CursoDetalleResponse> responseMock =
                new BaseListReponse<>(200, "OK", List.of(CURSO_1, CURSO_2));

        when(service.listarPorCicloAcademico(1)).thenReturn(responseMock);

        mockMvc.perform(get("/curso/listar-por-ciclo-academico/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].codigo", is("CUR-001")));
    }

    // --- BUSCAR POR ID ---
    @Test
    void buscarTest() throws Exception {
        BaseObjectResponse<CursoDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "OK", CURSO_1);

        when(service.buscar(eq(1))).thenReturn(responseMock);

        mockMvc.perform(get("/curso/buscar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.codigo", is("CUR-001")));
    }

    // --- INSERTAR ---
    @Test
    void insertarTest() throws Exception {
        CursoCreateRequest request = new CursoCreateRequest();
        request.setIdAsignatura(1);
        request.setIdEscuela(1);
        request.setIdCicloAcademico(1);
        request.setCiclo(1);
        request.setPlanDeEstudios(List.of("Ingeniería de Sistemas"));
        request.setHorarios(List.of(new HorarioCreateRequest("T", "LUNES", "08:00:00", "10:00:00", 2, 101)));

        BaseObjectResponse<CursoDetalleResponse> responseMock =
                new BaseObjectResponse<>(201, "CREATED", CURSO_1);

        when(service.registrar(any())).thenReturn(responseMock);

        mockMvc.perform(post("/curso/insertar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.codigo", is("CUR-001")));
    }

    // --- INSERTAR ALL ---
    @Test
    void insertarAllTest() throws Exception {
        List<CursoCreateRequest> requestList = List.of(
                new CursoCreateRequest(1, List.of("Ingeniería de Sistemas"), 1, 1, 1, "G1", List.of()),
                new CursoCreateRequest(2, List.of("Ingeniería Industrial"), 1, 1, 2, "G2", List.of())
        );

        BaseListReponse<CursoDetalleResponse> responseMock =
                new BaseListReponse<>(201, "CREATED", List.of(CURSO_1, CURSO_2));

        when(service.registrarAll(any())).thenReturn(responseMock);

        mockMvc.perform(post("/curso/insertar-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // --- ACTUALIZAR ---
    @Test
    void actualizarTest() throws Exception {
        CursoUpdateRequest request = new CursoUpdateRequest();
        request.setGrupo("G3");

        CURSO_1.setGrupo("G3");

        BaseObjectResponse<CursoDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "UPDATED", CURSO_1);

        when(service.actualizar(eq(1), any())).thenReturn(responseMock);

        mockMvc.perform(put("/curso/actualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.grupo", is("G3")));
    }

    // --- ELIMINAR ---
    @Test
    void eliminarTest() throws Exception {
        BaseObjectResponse<String> responseMock =
                new BaseObjectResponse<>(200, "OK", "Eliminado");

        when(service.eliminar(eq(1))).thenReturn(responseMock);

        mockMvc.perform(delete("/curso/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OK")));
    }
 */
}
