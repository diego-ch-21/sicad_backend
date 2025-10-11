package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.sicad.sicad_backend.utils.NumbersUtils.convertirARomano;


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

    public BaseObjectResponse<CicloAcademicoDetalleResponse> registrarCiclo(CicloAcademicoCreateRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate fechaInicio;
        LocalDate fechaFin;

        try {
            fechaInicio = LocalDate.parse(request.getFechaInicio(), formatter);
            fechaFin = LocalDate.parse(request.getFechaFin(), formatter);

            if (!fechaFin.isAfter(fechaInicio)) {
                return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
            }

        } catch (DateTimeParseException e) {
            return new BaseObjectResponse<>(400, "Formato de fecha inválido (debe ser dd-MM-yyyy)", null);
        }

        // Convertir periodo a romano
        String periodoRomano = convertirARomano(request.getPeriodo());

        String nombre = request.getAnio() + "-" + periodoRomano;

        // Validar que el nombre no exista ya en la base de datos
        boolean existeNombre = cicloAcademicoRepo.existsByNombre(nombre);
        if (existeNombre) {
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

        CicloAcademicoDetalleResponse dto = modelMapper.map(ciclo, CicloAcademicoDetalleResponse.class);
        return new BaseObjectResponse<>(201, "Ciclo académico registrado exitosamente", dto);
    }

    public BaseObjectResponse<CicloAcademicoDetalleResponse> actualizarCiclo(Integer id, CicloAcademicoUpdateRequest request) {
        CicloAcademico ciclo = cicloAcademicoRepo.findById(id).orElse(null);
        if (ciclo == null) {
            return new BaseObjectResponse<>(404, "Ciclo académico no encontrado", null);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (request.getAnio() != null) {
            ciclo.setAnio(request.getAnio());
        }

        if (request.getPeriodo() != null) {
            ciclo.setPeriodo(request.getPeriodo());
        }

        if (request.getFechaInicio() != null && !request.getFechaInicio().isBlank()) {
            try {
                ciclo.setFechaInicio(LocalDate.parse(request.getFechaInicio(), formatter));
            } catch (DateTimeParseException e) {
                return new BaseObjectResponse<>(400, "Formato de fechaInicio inválido (debe ser dd-MM-yyyy)", null);
            }
        }

        if (request.getFechaFin() != null && !request.getFechaFin().isBlank()) {
            try {
                ciclo.setFechaFin(LocalDate.parse(request.getFechaFin(), formatter));
            } catch (DateTimeParseException e) {
                return new BaseObjectResponse<>(400, "Formato de fechaFin inválido (debe ser dd-MM-yyyy)", null);
            }
        }

        if (ciclo.getFechaInicio() != null && ciclo.getFechaFin() != null &&
                !ciclo.getFechaFin().isAfter(ciclo.getFechaInicio())) {
            return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
        }

        // Actualizar nombre si cambió año o periodo
        if (request.getAnio() != null || request.getPeriodo() != null) {
            String periodoRomano = convertirARomano(ciclo.getPeriodo());
            String nuevoNombre = ciclo.getAnio() + "-" + periodoRomano;
            ciclo.setNombre(nuevoNombre);

        }

        cicloAcademicoRepo.save(ciclo);

        CicloAcademicoDetalleResponse dto = modelMapper.map(ciclo, CicloAcademicoDetalleResponse.class);
        return new BaseObjectResponse<>(200, "Ciclo académico actualizado exitosamente", dto);
    }
    public BaseObjectResponse<String> eliminarCicloAcademico(Integer idCicloAcademico) {
        // Validación de parámetro
        if (idCicloAcademico == null) {
            return new BaseObjectResponse<>(400, "idCicloAcademico no proporcionado", null);
        }

        // Validar existencia del curso
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
        if (cicloAcademico == null) {
            return new BaseObjectResponse<>(404, "CicloAcademico  no encontrado", null);
        }

        // desabilitar
        cicloAcademico.setEnabled(false);
        cicloAcademicoRepo.save(cicloAcademico);
        return new BaseObjectResponse<>(200, "se elimino el cicloAcademico exitosamente", null);
    }

    @Override
    public List<CicloAcademico> findByEnabledTrue() {
        return cicloAcademicoRepo.findByEnabledTrue();
    }
}