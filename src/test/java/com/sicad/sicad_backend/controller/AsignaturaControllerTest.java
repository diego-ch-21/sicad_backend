package com.sicad.sicad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
/*
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AsignaturaController.class)

 */
public class AsignaturaControllerTest {
    /*

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAsignaturaService service;

    @Autowired
    private ObjectMapper objectMapper;

    AsignaturaDetalleResponse ASIG_1 = new AsignaturaDetalleResponse(1, "PR404763", "Programación OO", true);
    AsignaturaDetalleResponse ASIG_2 = new AsignaturaDetalleResponse(2, "PR896216", "Base de Datos", true);


    @Test
    void listarTest() throws Exception {
        BaseListReponse<AsignaturaDetalleResponse> responseMock =
                new BaseListReponse<>(200, "OK", List.of(ASIG_1, ASIG_2));

        Mockito.when(service.listar()).thenReturn(responseMock);

        mockMvc.perform(get("/asignatura/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].nombre", is("PR404763")));
    }


    @Test
    void buscarTest() throws Exception {
        int id = 1;
        BaseObjectResponse<AsignaturaDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "OK", ASIG_1);

        Mockito.when(service.buscar(eq(id))).thenReturn(responseMock);

        mockMvc.perform(get("/asignatura/buscar/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre", is("PR404763")));
    }


    @Test
    void insertarTest() throws Exception {
        AsignaturaCreateRequest request = new AsignaturaCreateRequest("Redes", "Intro a Redes");

        BaseObjectResponse<AsignaturaDetalleResponse> responseMock =
                new BaseObjectResponse<>(201, "CREATED", ASIG_2);

        Mockito.when(service.registrar(any())).thenReturn(responseMock);

        mockMvc.perform(post("/asignatura/insertar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre", is("PR896216")));
    }


    @Test
    void insertarAllTest() throws Exception {
        List<AsignaturaCreateRequest> reqList = List.of(
                new AsignaturaCreateRequest("PR404763", "Programación"),
                new AsignaturaCreateRequest("PR896216", "Bases de Datos")
        );

        BaseListReponse<AsignaturaDetalleResponse> responseMock =
                new BaseListReponse<>(201, "CREATED", List.of(ASIG_1, ASIG_2));

        Mockito.when(service.registrarAll(any())).thenReturn(responseMock);

        mockMvc.perform(post("/asignatura/insertar-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }


    @Test
    void actualizarTest() throws Exception {
        int id = 1;
        AsignaturaUpdateRequest request =
                new AsignaturaUpdateRequest("PR404763 Avanzado", "Actualización");

        ASIG_1.setNombre("PR404763 Avanzado");

        BaseObjectResponse<AsignaturaDetalleResponse> responseMock =
                new BaseObjectResponse<>(200, "UPDATED", ASIG_1);

        Mockito.when(service.actualizar(eq(id), any())).thenReturn(responseMock);

        mockMvc.perform(put("/asignatura/actualizar/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre", is("PR404763 Avanzado")));
    }


    @Test
    void eliminarTest() throws Exception {
        int id = 1;
        BaseObjectResponse<String> responseMock =
                new BaseObjectResponse<>(200, "OK", "Eliminado");

        Mockito.when(service.eliminar(eq(id))).thenReturn(responseMock);

        mockMvc.perform(delete("/asignatura/eliminar/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OK")));
    }
    */
}
