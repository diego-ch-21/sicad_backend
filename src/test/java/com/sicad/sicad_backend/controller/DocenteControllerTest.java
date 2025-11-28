package com.sicad.sicad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.docente.DocenteCreateRequest;
import com.sicad.sicad_backend.dto.docente.DocenteDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = DocenteController.class)

 */
public class DocenteControllerTest {
    /*

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IDocenteService service;

    // --- TEST: Listar Docentes ---
    @Test
    void testListarDocentes() throws Exception {
        BaseListReponse<DocenteDetalleResponse> response =
                new BaseListReponse<>(200, "OK", Collections.emptyList());

        when(service.listar()).thenReturn(response);

        mockMvc.perform(get("/docente/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // --- TEST: Buscar por ID ---
    @Test
    void testBuscarDocentePorId() throws Exception {
        DocenteDetalleResponse docente = new DocenteDetalleResponse();
        docente.setIdDocente(1);

        BaseObjectResponse<DocenteDetalleResponse> response =
                new BaseObjectResponse<>(200, "OK", docente);

        when(service.buscar(1)).thenReturn(response);

        mockMvc.perform(get("/docente/buscar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.idDocente").value(1));
    }

    // --- TEST: Registrar Docente ---
    @Test
    void testRegistrarDocente() throws Exception {
        DocenteCreateRequest request = new DocenteCreateRequest(
                "test@example.com", "123456", "Juan", "Perez", 1, 1, "ABC123");

        DocenteDetalleResponse docente = new DocenteDetalleResponse();
        docente.setIdDocente(10);

        BaseObjectResponse<DocenteDetalleResponse> response =
                new BaseObjectResponse<>(201, "CREATED", docente);

        when(service.registrar(any(DocenteCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/docente/insertar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.idDocente").value(10));
    }

    // --- TEST: Error de Validación ---
    @Test
    void testRegistrarDocenteValidacion() throws Exception {
        DocenteCreateRequest request = new DocenteCreateRequest(); // Vacío → debe fallar

        mockMvc.perform(post("/docente/insertar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

     */
}
