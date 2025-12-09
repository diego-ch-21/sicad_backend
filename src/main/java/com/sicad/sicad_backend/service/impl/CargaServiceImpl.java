package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.CicloCargaCurso.AsignaturaAgrupadaResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoAgrupadoResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoConDocenteResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.HorarioCursoResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargaServiceImpl
        extends CRUDImpl<Carga, Integer>
        implements ICargaService {

    private final ICargaRepo  cargaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ICursoRepo cursoRepo;
    private final IEscuelaRepo escuelaRepo;
    private final IAsignacionRepo asignacionRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Carga, Integer> getRepo() {
        return cargaRepo;
    }

    @Override
    public BaseListReponse<CargaDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico) {

        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if (cicloOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        List<CargaDetalleResponse> lista = cargaRepo.findByIdCicloAcademicoAndEnabledTrue(idCicloAcademico)
                .stream()
                .map(this::convCargaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.CARGA.listado(), lista);
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> buscar(Integer idCarga) {
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = cargaOpt.get();

        return new BaseObjectResponse<>(200, Modulo.CARGA.encontrado(), convCargaDetalle(carga));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idCarga) {
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = cargaOpt.get();
        carga.setEnabled(false);
        carga.setPrincipal(false);
        cargaRepo.save(carga);

        return new BaseObjectResponse<>(200, Modulo.CARGA.eliminado(), null);
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> asignarPrincipal(Integer idCicloAcademico,Integer idCarga) {
        //verificar carga
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga cargaNew = cargaOpt.get();
        //verificar ciclo academico
        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if (cicloAcademicoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        if(!idCicloAcademico.equals(cargaNew.getCicloAcademico().getIdCicloAcademico())){
            return new BaseObjectResponse<>(404, Modulo.CARGA.noPertenece(Modulo.CICLO_ACADEMICO), null);
        }

        if(cargaNew.getPrincipal()){
            return new BaseObjectResponse<>(200, Modulo.CARGA.principalYaSeleccionado(), convCargaDetalle(cargaNew));
        }

        Optional<Carga> cargaPrincipalAnterior = cargaRepo.findPrincipalByCicloAcademicoAndEnabledTrue(idCicloAcademico);
        if(!cargaPrincipalAnterior.isEmpty()){
            Carga cargaPrincipal = cargaPrincipalAnterior.get();
            cargaPrincipal.setPrincipal(false);
            cargaRepo.save(cargaPrincipal);
        }

        cargaNew.setPrincipal(true);
        cargaRepo.save(cargaNew);

        return new BaseObjectResponse<>(201, Modulo.CARGA.principalSeleccionado(), convCargaDetalle(cargaNew));
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> buscarPrincipal(Integer idCicloAcademico) {
        Optional<Carga> optCarga = cargaRepo.findPrincipalByCicloAcademicoAndEnabledTrue(idCicloAcademico);
        if (optCarga.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = optCarga.get();
        return new BaseObjectResponse<>(201, Modulo.CARGA.encontrado(),convCargaDetalle(carga));
    }

    public void exportarCargaElectiva(Integer idCarga) {


    }
    public void exportarCargaElectivaDocente(Integer docentem,Integer idCarga){

    }

    //---------------------------------------------------------------------
    @Override
    public BaseListReponse<CursoAgrupadoResponse> listarCursosAgrupados(
            Integer idCicloAcademico,
            Integer idCarga
    ) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if(cicloOpt.isEmpty()){
            return  new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if(cargaOpt.isEmpty()){
            return  new BaseListReponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }

        // 1) Cursos del ciclo académico
        List<Curso> cursos = cursoRepo.findByEnabledTrueAndCicloAcademico(idCicloAcademico);

        // 2) Asignaciones asociadas a la CARGA
        List<Asignacion> asignaciones = asignacionRepo.findByEscuelaEnabled(idCarga);

        // Mapa rápido: idCurso → asignación
        Map<Integer, Asignacion> mapAsignaciones = asignaciones.stream()
                .collect(Collectors.toMap(a -> a.getCurso().getIdCurso(), a -> a, (x, y) -> x));

        // 3) Agrupar por ciclo → asignatura
        Map<Integer, Map<String, List<Curso>>> agrupado =
                cursos.stream()
                        .collect(Collectors.groupingBy(
                                Curso::getCiclo,
                                Collectors.groupingBy(c ->
                                        c.getAsignatura().getNombre() + "||" + c.getAsignatura().getCodigo()
                                )
                        ));

        // 4) Convertir a DTO final
        List<CursoAgrupadoResponse> lista =
                agrupado.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entryCiclo -> {

                            Integer ciclo = entryCiclo.getKey();
                            Map<String, List<Curso>> asigMap = entryCiclo.getValue();

                            List<AsignaturaAgrupadaResponse> asignaturas =
                                    asigMap.entrySet().stream()
                                            .sorted(Map.Entry.comparingByKey())
                                            .map(entryAsig -> {

                                                String[] partes = entryAsig.getKey().split("\\|\\|");
                                                String nombreAsig = partes[0];
                                                String codigoAsig = partes[1];

                                                List<CursoConDocenteResponse> cursosDTO =
                                                        entryAsig.getValue().stream()
                                                                .map(curso -> {

                                                                    // --------------- DOCENTE ---------------
                                                                    Asignacion asign = mapAsignaciones.get(curso.getIdCurso());

                                                                    Integer idDocente = null;
                                                                    String nombreDocente = "";

                                                                    if (asign != null && asign.getDocente() != null) {
                                                                        idDocente = asign.getDocente().getIdDocente();
                                                                        nombreDocente =
                                                                                asign.getDocente().getUsuario().getNombre() + " " +
                                                                                        asign.getDocente().getUsuario().getApellido();
                                                                    }

                                                                    // --------------- HORARIOS ---------------
                                                                    List<HorarioCursoResponse> horarios = curso.getHorarios().stream()
                                                                            .filter(Horario::getEnabled)
                                                                            .map(h -> new HorarioCursoResponse(
                                                                                    h.getDiaSemana(),
                                                                                    h.getHoraInicio(),
                                                                                    h.getHoraFin(),
                                                                                    h.getTipoSesion(),
                                                                                    (h.getAula() != null)
                                                                                            ? h.getAula().getNombre()
                                                                                            : ""
                                                                            ))
                                                                            .toList();

                                                                    // --------------- CURSO DTO ---------------
                                                                    return new CursoConDocenteResponse(
                                                                            curso.getIdCurso(),
                                                                            curso.getCodigo(),
                                                                            curso.getGrupo(),
                                                                            curso.getPlanDeEstudios(),
                                                                            curso.getEscuela().getNombre(),
                                                                            idDocente,
                                                                            nombreDocente,
                                                                            horarios
                                                                    );

                                                                }).toList();

                                                return new AsignaturaAgrupadaResponse(
                                                        nombreAsig,
                                                        codigoAsig,
                                                        cursosDTO
                                                );

                                            }).toList();

                            return new CursoAgrupadoResponse(ciclo, asignaturas);
                        })
                        .toList();

        return new BaseListReponse<>(200, Modulo.CURSO.listado(), lista);
    }

    @Override
    public BaseListReponse<CursoAgrupadoResponse> listarCursosAgrupadosPorEscuela(
            Integer idCicloAcademico,
            Integer idCarga,
            Integer idEscuela
    ) {
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if(cicloOpt.isEmpty()){
            return  new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if(cargaOpt.isEmpty()){
            return  new BaseListReponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(idEscuela);
        if(escuelaOpt.isEmpty()){
            return  new BaseListReponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }

        // 1) Cursos del ciclo filtrados por ESCUELA
        List<Curso> cursos = cursoRepo.findByEnabledTrueAndCicloAcademico(idCicloAcademico)
                .stream()
                .filter(c -> c.getEscuela().getIdEscuela().equals(idEscuela))
                .toList();

        // 2) Asignaciones de la carga
        List<Asignacion> asignaciones = asignacionRepo.findByEscuelaEnabled(idCarga);

        // Mapa rápido: idCurso → asignación
        Map<Integer, Asignacion> mapAsignaciones = asignaciones.stream()
                .collect(Collectors.toMap(
                        a -> a.getCurso().getIdCurso(),
                        a -> a,
                        (x, y) -> x
                ));

        // 3) Agrupar por ciclo → asignatura
        Map<Integer, Map<String, List<Curso>>> agrupado =
                cursos.stream()
                        .collect(Collectors.groupingBy(
                                Curso::getCiclo,
                                Collectors.groupingBy(c ->
                                        c.getAsignatura().getNombre() + "||" + c.getAsignatura().getCodigo()
                                )
                        ));

        // 4) Construir respuesta final
        List<CursoAgrupadoResponse> lista =
                agrupado.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entryCiclo -> {

                            Integer ciclo = entryCiclo.getKey();
                            Map<String, List<Curso>> asigMap = entryCiclo.getValue();

                            List<AsignaturaAgrupadaResponse> asignaturas =
                                    asigMap.entrySet().stream()
                                            .sorted(Map.Entry.comparingByKey())
                                            .map(entryAsig -> {

                                                String[] parts = entryAsig.getKey().split("\\|\\|");
                                                String nombreAsig = parts[0];
                                                String codigoAsig = parts[1];

                                                List<CursoConDocenteResponse> cursosDTO =
                                                        entryAsig.getValue().stream()
                                                                .map(curso -> {

                                                                    // --------------- Docente ---------------
                                                                    Asignacion asign = mapAsignaciones.get(curso.getIdCurso());

                                                                    Integer idDocente = null;
                                                                    String nombreDocente = "";

                                                                    if (asign != null && asign.getDocente() != null) {
                                                                        idDocente = asign.getDocente().getIdDocente();
                                                                        nombreDocente =
                                                                                asign.getDocente().getUsuario().getNombre() + " " +
                                                                                        asign.getDocente().getUsuario().getApellido();
                                                                    }

                                                                    // --------------- Horarios ---------------
                                                                    List<HorarioCursoResponse> horarios =
                                                                            curso.getHorarios().stream()
                                                                                    .filter(Horario::getEnabled)
                                                                                    .map(h -> new HorarioCursoResponse(
                                                                                            h.getDiaSemana(),
                                                                                            h.getHoraInicio(),
                                                                                            h.getHoraFin(),
                                                                                            h.getTipoSesion(),
                                                                                            (h.getAula() != null)
                                                                                                    ? h.getAula().getNombre()
                                                                                                    : ""
                                                                                    ))
                                                                                    .toList();

                                                                    // --------------- Curso DTO ---------------
                                                                    return new CursoConDocenteResponse(
                                                                            curso.getIdCurso(),
                                                                            curso.getCodigo(),
                                                                            curso.getGrupo(),
                                                                            curso.getPlanDeEstudios(),
                                                                            curso.getEscuela().getNombre(),
                                                                            idDocente,
                                                                            nombreDocente,
                                                                            horarios
                                                                    );
                                                                })
                                                                .toList();

                                                return new AsignaturaAgrupadaResponse(
                                                        nombreAsig,
                                                        codigoAsig,
                                                        cursosDTO
                                                );
                                            })
                                            .toList();

                            return new CursoAgrupadoResponse(ciclo, asignaturas);
                        })
                        .toList();

        return new BaseListReponse<>(200, Modulo.CARGA.listado(), lista);
    }




    private CargaDetalleResponse convCargaDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }
}
