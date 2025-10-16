package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sicad.sicad_backend.utils.NumbersUtils.convertirARomano;

@Slf4j
@Service
@RequiredArgsConstructor
public class CicloAcademicoServiceImpl
        extends CRUDImpl<CicloAcademico, Integer>
        implements ICicloAcademicoService {

    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<CicloAcademico, Integer> getRepo() {
        return cicloAcademicoRepo;
    }

    @Override
    public BaseListReponse<CicloAcademicoDetalleResponse> listar() {
        List<CicloAcademicoDetalleResponse> lista = cicloAcademicoRepo.findByEnabledTrue()
                .stream()
                .map(this::convCicloAcademicoDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.CICLO_ACADEMICO.listado(), lista);
    }

    @Override
    public BaseObjectResponse<CicloAcademicoDetalleResponse> buscar(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);

        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.CICLO_ACADEMICO.encontrado(), convCicloAcademicoDetalle(cicloOpt.get()));
    }

    @Override
    public BaseObjectResponse<CicloAcademicoDetalleResponse> registrar(CicloAcademicoCreateRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaInicio;
        LocalDate fechaFin;
        fechaInicio = LocalDate.parse(request.getFechaInicio(), formatter);
        fechaFin = LocalDate.parse(request.getFechaFin(), formatter);
        if (!fechaFin.isAfter(fechaInicio)) {
            return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
        }

        String periodoRomano = convertirARomano(request.getPeriodo());
        String nombre = request.getAnio() + "-" + periodoRomano;

        if (cicloAcademicoRepo.existsByNombre(nombre)) {
            return new BaseObjectResponse<>(409, "Ya existe un ciclo académico con ese nombre", null);
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .anio(request.getAnio())
                .periodo(request.getPeriodo())
                .nombre(nombre)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .enabled(true)
                .build();

        cicloAcademicoRepo.save(ciclo);

        return new BaseObjectResponse<>(201, Modulo.CICLO_ACADEMICO.registrado(),
                convCicloAcademicoDetalle(ciclo));
    }

    @Override
    public BaseListReponse<CicloAcademicoDetalleResponse> registrarAll(List<CicloAcademicoCreateRequest> requests) {
        List<CicloAcademicoDetalleResponse> registrados = new ArrayList<>();
        int errores = 0;

        for (CicloAcademicoCreateRequest req : requests) {
            try {
                BaseObjectResponse<CicloAcademicoDetalleResponse> resp = registrar(req);
                if (resp.status() == 201 && resp.data() != null) {
                    registrados.add(resp.data());
                } else {
                    errores++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errores++;
            }
        }

        return new BaseListReponse<>(201,
                Modulo.CICLO_ACADEMICO.resumenAllRegistro(registrados.size(), errores),
                registrados);
    }

    @Override
    public BaseObjectResponse<CicloAcademicoDetalleResponse> actualizar(Integer idCicloAcademico, CicloAcademicoUpdateRequest request) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);

        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        CicloAcademico ciclo = cicloOpt.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (request.getAnio() != null) ciclo.setAnio(request.getAnio());
        if (request.getPeriodo() != null) ciclo.setPeriodo(request.getPeriodo());

        if (request.getFechaInicio() != null && !request.getFechaInicio().isBlank()) {
            ciclo.setFechaInicio(LocalDate.parse(request.getFechaInicio(), formatter));
        }
        if (request.getFechaFin() != null && !request.getFechaFin().isBlank()) {
            ciclo.setFechaFin(LocalDate.parse(request.getFechaFin(), formatter));
        }

        if (ciclo.getFechaInicio() != null && ciclo.getFechaFin() != null &&
                !ciclo.getFechaFin().isAfter(ciclo.getFechaInicio())) {
            return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
        }

        String periodoRomano = convertirARomano(ciclo.getPeriodo());
        ciclo.setNombre(ciclo.getAnio() + "-" + periodoRomano);

        cicloAcademicoRepo.save(ciclo);

        return new BaseObjectResponse<>(200, Modulo.CICLO_ACADEMICO.actualizado(), convCicloAcademicoDetalle(ciclo));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);

        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        CicloAcademico ciclo = cicloOpt.get();
        ciclo.setEnabled(false);
        cicloAcademicoRepo.save(ciclo);

        return new BaseObjectResponse<>(200, Modulo.CICLO_ACADEMICO.eliminado(), null);
    }
    private CicloAcademicoDetalleResponse convCicloAcademicoDetalle(CicloAcademico obj) {
        return modelMapper.map(obj, CicloAcademicoDetalleResponse.class);
    }

}