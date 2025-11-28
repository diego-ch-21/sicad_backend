package com.sicad.sicad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/*
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PlanDeEstudioController.class)

 */
public class PlanDeEstudioControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPlanDeEstudioService service;

    @Autowired
    private ObjectMapper objectMapper;

    PlanDeEstudioDetalleResponse PLAN_1 = new PlanDeEstudioDetalleResponse(1, "Ingeniería de Sistemas", true);
    PlanDeEstudioDetalleResponse PLAN_2 = new PlanDeEstudioDetalleResponse(2, "Ingeniería Industrial", true);

    // --- LISTAR ---
    @Test
    void listarTest() throws Exception {
        BaseListReponse<PlanDeEstudioDetalleResponse> responseMock =
                new BaseListReponse<>(200, "OK", List.of(PLAN_1, PLAN_2));

        when(service.listar()).thenReturn(responseMock);

        mockMvc.perform(get("/plan-de-estudio/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].nombre", is("Ingeniería de Sistemas")));
    }

    // --- BUSCAR POR ID ---
    @Test
    void buscarTest() throws Exception {
        int id = 1;
        BaseObjectResponse<PlanDeEstudioDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "OK", PLAN_1);

        when(service.buscar(eq(id))).thenReturn(responseMock);

        mockMvc.perform(get("/plan-de-estudio/buscar/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre", is("Ingeniería de Sistemas")));
    }

    // --- INSERTAR ---
    @Test
    void insertarTest() throws Exception {
        PlanDeEstudioCreateRequest request = new PlanDeEstudioCreateRequest("Arquitectura");

        BaseObjectResponse<PlanDeEstudioDetalleResponse> responseMock =
                new BaseObjectResponse<>(201, "CREATED", PLAN_2);

        when(service.registrar(any())).thenReturn(responseMock);

        mockMvc.perform(post("/plan-de-estudio/insertar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre", is("Ingeniería Industrial")));
    }

    // --- INSERTAR ALL ---
    @Test
    void insertarAllTest() throws Exception {
        List<PlanDeEstudioCreateRequest> reqList = List.of(
                new PlanDeEstudioCreateRequest("Ingeniería de Sistemas"),
                new PlanDeEstudioCreateRequest("Ingeniería Industrial")
        );

        BaseListReponse<PlanDeEstudioDetalleResponse> responseMock =
                new BaseListReponse<>(201, "CREATED", List.of(PLAN_1, PLAN_2));

        when(service.registrarAll(any())).thenReturn(responseMock);

        mockMvc.perform(post("/plan-de-estudio/insertar-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    // --- ACTUALIZAR ---
    @Test
    void actualizarTest() throws Exception {
        int id = 1;
        PlanDeEstudioUpdateRequest request = new PlanDeEstudioUpdateRequest("Ingeniería de Sistemas Avanzado");
        PLAN_1.setNombre("Ingeniería de Sistemas Avanzado");

        BaseObjectResponse<PlanDeEstudioDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "UPDATED", PLAN_1);

        when(service.actualizar(eq(id), any())).thenReturn(responseMock);

        mockMvc.perform(put("/plan-de-estudio/actualizar/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre", is("Ingeniería de Sistemas Avanzado")));
    }

    // --- ELIMINAR ---
    @Test
    void eliminarTest() throws Exception {
        int id = 1;
        BaseObjectResponse<String> responseMock =
                new BaseObjectResponse<>(200, "OK", "Eliminado");

        when(service.eliminar(eq(id))).thenReturn(responseMock);

        mockMvc.perform(delete("/plan-de-estudio/eliminar/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OK")));
    }

 */
}
