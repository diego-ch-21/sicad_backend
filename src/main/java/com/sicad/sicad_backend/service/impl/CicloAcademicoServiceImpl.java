package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.Enum.TipoFile;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.AsignaturaAgrupadaResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoAgrupadoResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoConDocenteResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.HorarioCursoResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoFileResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
import com.sicad.sicad_backend.service.interfaces.INoficacionService;
import com.sicad.sicad_backend.service.interfaces.ISupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sicad.sicad_backend.utils.NumbersUtils.convertirARomano;

@Slf4j
@Service
@RequiredArgsConstructor
public class CicloAcademicoServiceImpl
        extends CRUDImpl<CicloAcademico, Integer>
        implements ICicloAcademicoService {

    private final ICicloAcademicoRepo cicloAcademicoRepo;

    private final INoficacionService notificacionService;
    private final ISupabaseStorageService storageService;
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
    public BaseObjectResponse<CicloAcademicoFileResponse> buscarFile(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);

        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.CICLO_ACADEMICO.encontrado(), convCicloAcademicoFile(cicloOpt.get()));
    }

    @Override
    public BaseObjectResponse<CicloAcademicoDetalleResponse> registrar(CicloAcademicoCreateRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicio = LocalDate.parse(request.getFechaInicio(), formatter);
        LocalDate fechaFin = LocalDate.parse(request.getFechaFin(), formatter);

        if (!fechaFin.isAfter(fechaInicio)) {
            return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
        }

        // Manejar el periodo 0 de forma explícita
        String periodoTexto;
        if (request.getPeriodo() == 0) {
            periodoTexto = "0"; // o "Especial", "Anual", según tu dominio
        } else {
            periodoTexto = convertirARomano(request.getPeriodo());
        }

        String nombre = request.getAnio() + "-" + periodoTexto;

        if (cicloAcademicoRepo.existsByNombre(nombre)) {
            return new BaseObjectResponse<>(409, "Ya existe el ciclo académico " + nombre, null);
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

        return new BaseObjectResponse<>(201, Modulo.CICLO_ACADEMICO.registrado(), convCicloAcademicoDetalle(ciclo));
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
        // Buscar el ciclo académico activo
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        CicloAcademico ciclo = cicloOpt.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Actualizar año si fue enviado
        if (request.getAnio() != null) {
            ciclo.setAnio(request.getAnio());
        }

        // Actualizar periodo si fue enviado
        if (request.getPeriodo() != null) {
            ciclo.setPeriodo(request.getPeriodo());
        }

        // Actualizar fechas si fueron enviadas
        if (request.getFechaInicio() != null && !request.getFechaInicio().isBlank()) {
            ciclo.setFechaInicio(LocalDate.parse(request.getFechaInicio().trim(), formatter));
        }
        if (request.getFechaFin() != null && !request.getFechaFin().isBlank()) {
            ciclo.setFechaFin(LocalDate.parse(request.getFechaFin().trim(), formatter));
        }

        // Validar coherencia de fechas (si ambas existen)
        if (ciclo.getFechaInicio() != null && ciclo.getFechaFin() != null &&
                !ciclo.getFechaFin().isAfter(ciclo.getFechaInicio())) {
            return new BaseObjectResponse<>(400, "La fecha fin debe ser posterior a la fecha inicio", null);
        }

        // Regenerar nombre del ciclo académico (ej: "2025-II")
        String periodoRomano = convertirARomano(ciclo.getPeriodo());
        String nuevoNombre = ciclo.getAnio() + "-" + periodoRomano;

        // Validar duplicado solo si el nombre cambió
        if (!nuevoNombre.equals(ciclo.getNombre()) && cicloAcademicoRepo.existsByNombre(nuevoNombre)) {
            return new BaseObjectResponse<>(409, "Ya existe el ciclo académico  "+ciclo.getNombre(), null);
        }

        ciclo.setNombre(nuevoNombre);

        // Guardar cambios
        cicloAcademicoRepo.save(ciclo);

        // Devolver respuesta con los datos actualizados
        return new BaseObjectResponse<>(200, Modulo.CICLO_ACADEMICO.actualizado(), convCicloAcademicoDetalle(ciclo));
    }

    @Override
    public BaseObjectResponse<CicloAcademicoFileResponse> actualizarFilePdf(Integer idCicloAcademico, MultipartFile filePdf) {
        try {
            Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
            if (cicloOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
            }

            if (filePdf == null || filePdf.isEmpty()) {
                return new BaseObjectResponse<>(400, "Debe enviar un archivo PDF", null);
            }

            TipoFile tipo = storageService.getTipoFile(filePdf);
            if (!tipo.equals(TipoFile.PDF)) {
                return new BaseObjectResponse<>(400, "El archivo debe ser un PDF válido", null);
            }

            CicloAcademico ciclo = cicloOpt.get();

            // Eliminar archivo anterior
            if (ciclo.getUrlPdf() != null && !ciclo.getUrlPdf().isEmpty()) {
                storageService.deleteFile(ciclo.getUrlPdf());
            }

            String nuevoUrl = storageService.uploadFile(filePdf, "documents");
            ciclo.setUrlPdf(nuevoUrl);
            cicloAcademicoRepo.save(ciclo);

            return new BaseObjectResponse<>(200, "PDF actualizado correctamente", convCicloAcademicoFile(ciclo));

        } catch (Exception e) {
            log.error("Error al actualizar PDF de ciclo académico", e);
            return new BaseObjectResponse<>(500, "Error al actualizar el PDF de ciclo académico", null);
        }
    }
    @Override
    public BaseObjectResponse<CicloAcademicoFileResponse> actualizarFileExcel(Integer idCicloAcademico, MultipartFile fileExcel) {
        try {
            Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
            if (cicloOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
            }

            if (fileExcel == null || fileExcel.isEmpty()) {
                return new BaseObjectResponse<>(400, "Debe enviar un archivo Excel", null);
            }

            TipoFile tipo = storageService.getTipoFile(fileExcel);
            if (!tipo.equals(TipoFile.EXCEL)) {
                return new BaseObjectResponse<>(400, "El archivo debe ser un Excel válido", null);
            }

            CicloAcademico ciclo = cicloOpt.get();

            if (ciclo.getUrlExcel() != null && !ciclo.getUrlExcel().isEmpty()) {
                storageService.deleteFile(ciclo.getUrlExcel());
            }

            String nuevoUrl = storageService.uploadFile(fileExcel, "documents");
            ciclo.setUrlExcel(nuevoUrl);
            cicloAcademicoRepo.save(ciclo);

            return new BaseObjectResponse<>(200, "Excel actualizado correctamente", convCicloAcademicoFile(ciclo));

        } catch (Exception e) {
            log.error("Error al actualizar Excel de ciclo académico", e);
            return new BaseObjectResponse<>(500, "Error al actualizar el Excel de ciclo académico", null);
        }
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

    private CicloAcademicoFileResponse convCicloAcademicoFile(CicloAcademico obj) {
        return modelMapper.map(obj, CicloAcademicoFileResponse.class);
    }

}