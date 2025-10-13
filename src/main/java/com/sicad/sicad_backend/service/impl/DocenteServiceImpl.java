package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sicad.sicad_backend.Enum.Message.CORREO_EN_USO;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocenteServiceImpl
        extends CRUDImpl<Docente, Integer>
        implements IDocenteService {

    private final IDocenteRepo docenteRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepo;
    private final ICategoriaRepo categoriaRepo;
    private final IDedicacionRepo dedicacionRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ICargaRepo cargaRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Docente, Integer> getRepo() {
        return docenteRepo;
    }


    @Override
    public BaseListReponse<DocenteDetalleResponse> listar() {
        List<DocenteDetalleResponse> lista = docenteRepo.findByEnabledTrue()
                .stream()
                .map(this::convDocenteDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.DOCENTE.listado(), lista);
    }

    @Override
    public BaseObjectResponse<DocenteDetalleResponse> buscar(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Docente docente = docenteOpt.get();
        return new BaseObjectResponse<>(200, Modulo.DOCENTE.encontrado(), convDocenteDetalle(docente));
    }

    @Override
    public BaseObjectResponse<DocenteDetalleResponse> buscarPorUsuario(Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByIdAndEnabledTrue(idUsuario);
        if(usuarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.USUARIO.noEncontrado(), null);
        }
        Usuario usuario = usuarioOpt.get();

        if(usuario.getRol().getIdRol() != 3) {
            return new BaseObjectResponse<>(404, "El usuario no es un docente", null);

        }
        Optional<Docente> docenteOpt = docenteRepo.findByIdUsuarioAndEnabledTrue(idUsuario);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noPertenece(Modulo.USUARIO), null);
        }
        Docente docente = docenteOpt.get();

        return new BaseObjectResponse<>(201, "Docente encontrado exitosamente", convDocenteDetalle(docente));
    }

    @Override
    public BaseObjectResponse<DocenteDetalleResponse> registrar(DocenteCreateRequest request) {

        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
        }

        Rol rol = rolRepo.findById(3).orElse(null); // Suponiendo que 3 = DOCENTE
        if (rol == null){
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);

        }
        Optional<Dedicacion> dedicacionOpt = dedicacionRepo.findByIdAndEnabledTrue(request.getIdDedicacion());
        if (dedicacionOpt.isEmpty()){
            return new BaseObjectResponse<>(404, Modulo.DEDICACION.noEncontrado(), null);
        }

        Optional<Categoria> categoriaOpt= categoriaRepo.findByIdAndEnabledTrue(request.getIdCategoria());
        if (categoriaOpt.isEmpty()){
            return new BaseObjectResponse<>(404,Modulo.CATEGORIA.noEncontrado(), null);

        }
        LocalDate fecha = LocalDate.now();
        String anioDosDigitos = String.valueOf(fecha.getYear()).substring(2);

        String codigoUsuario;
        do {
            codigoUsuario = anioDosDigitos+CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (usuarioRepo.existsByCodigo(codigoUsuario));


        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .codigo(codigoUsuario)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(fecha)
                .rol(rol)
                .build();
        usuarioRepo.save(usuario);


        String codigoDocente;
        do {
            codigoDocente = anioDosDigitos+CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (docenteRepo.existsByCodigo(codigoDocente));


        Docente docente = Docente.builder()
                .usuario(usuario)
                .dedicacion(dedicacionOpt.get())
                .categoria(categoriaOpt.get())
                .codigo(codigoDocente)
                .enabled(true)
                .build();
        docenteRepo.save(docente);

        return new BaseObjectResponse<>(201, Modulo.DOCENTE.registrado(), convDocenteDetalle(docente));
    }

    @Override
    public BaseListReponse<DocenteDetalleResponse> registrarAll(List<DocenteCreateRequest> requests) {
        List<DocenteDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (DocenteCreateRequest request : requests) {
            try {
                BaseObjectResponse<DocenteDetalleResponse> response = registrar(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorCount++;
            }
        }
        return new BaseListReponse<>(201,Modulo.DOCENTE.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<DocenteDetalleResponse> actualizar(Integer idDocente, DocenteUpdateRequest request) {

        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Docente docente = docenteOpt.get();

        // Validar email si viene en el request
        if (request.getEmail() != null && usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
        }

        // Obtener el usuario vinculado
        Usuario usuario = docente.getUsuario();

        // Actualizar campos SOLO si vienen
        if (request.getEmail() != null) usuario.setEmail(request.getEmail());
        if (request.getNombre() != null) usuario.setNombre(request.getNombre());
        if (request.getApellido() != null) usuario.setApellido(request.getApellido());
        if (request.getPassword() != null) usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepo.save(usuario);

        // Validar y actualizar dedicación solo si viene
        if (request.getIdDedicacion() != null) {
            Optional<Dedicacion> dedicacionOpt = dedicacionRepo.findByIdAndEnabledTrue(request.getIdDedicacion());
            if (dedicacionOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DEDICACION.noEncontrado(), null);
            }
            docente.setDedicacion(dedicacionOpt.get());
        }

        // Validar y actualizar categoría solo si viene
        if (request.getIdCategoria() != null) {
            Optional<Categoria> categoriaOpt = categoriaRepo.findByIdAndEnabledTrue(request.getIdCategoria());
            if (categoriaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CATEGORIA.noEncontrado(), null);
            }
            docente.setCategoria(categoriaOpt.get());
        }

        docenteRepo.save(docente);

        return new BaseObjectResponse<>(200, Modulo.DOCENTE.actualizado(), convDocenteDetalle(docente));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Docente docente = docenteOpt.get();
        docente.setEnabled(false);
        docenteRepo.save(docente);
        return new BaseObjectResponse<>(200, Modulo.DOCENTE.eliminado(), null);
    }
    @Override
    public BaseListReponse<DocenteEspecializacionResponse> listarDocentesConEspecializaciones() {
        List<Docente> docentes = docenteRepo.findByEnabledTrue();

        List<DocenteEspecializacionResponse> response = docentes.stream()
                .map(docente -> {
                    // Filtrar solo especializaciones habilitadas
                    List<Especializacion> especializacionesFiltradas = docente.getEspecializaciones()
                            .stream()
                            .filter(Especializacion::getEnabled) // o .getEnabled() si es Boolean
                            .toList();

                    // Reemplazar la lista original con la filtrada
                    docente.setEspecializaciones(especializacionesFiltradas);

                    return convDocenteEspecializacion(docente);
                })
                .toList();

        return new BaseListReponse<>(200, Modulo.DOCENTE.listado(), response);
    }

    @Override
    public BaseListReponse<DocentePreferenciaResponse> listarDocentesConPreferencias(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloAcademicoOpt =  cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if(cicloAcademicoOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        List<Docente> docentes = docenteRepo.findByEnabledTrue();

        List<DocentePreferenciaResponse> response = docentes.stream()
                .map(docente -> {

                    List<Preferencia> preferenciaFiltradas = docente.getPreferencias()
                            .stream()
                            .filter(pref -> pref.getEnabled() &&
                                    pref.getCicloAcademico().getIdCicloAcademico().equals(idCicloAcademico))  // <-- nuevo filtro
                            .toList();

                    docente.setPreferencias(preferenciaFiltradas);

                    return convDocentePreferencia(docente);
                })
                .toList();

        return new BaseListReponse<>(200,  Modulo.DOCENTE.listado(), response);
    }

    @Override
    public BaseListReponse<DocenteDisponibilidadResponse> listarDocentesConDisponibilidad(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloAcademicoOpt =  cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if(cicloAcademicoOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        List<Docente> docentes = docenteRepo.findByEnabledTrue();
        List<DocenteDisponibilidadResponse> response = docentes.stream()
                .map(docente -> {

                    List<Disponibilidad> disponibilidadFiltradas = docente.getDisponibilidad()
                            .stream()
                            .filter(dis -> dis.getEnabled() &&
                                    dis.getCicloAcademico().getIdCicloAcademico().equals(idCicloAcademico))  // <-- nuevo filtro
                            .toList();

                    docente.setDisponibilidad(disponibilidadFiltradas);

                    return convDocenteDisponibilidad(docente);
                })
                .toList();

        return new BaseListReponse<>(200,  Modulo.DOCENTE.listado(), response);
    }

    @Override
    public BaseListReponse<DocenteAsignacionResponse> listarDocentesCargaConAsignaciones(Integer idCarga) {
        Optional<Carga> cargaOpt =  cargaRepo.findByIdAndEnabledTrue(idCarga);
        if(cargaOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        List<Docente> docentes = docenteRepo.findByEnabledTrue();
        List<DocenteAsignacionResponse> response = docentes.stream()
                .map(docente -> {

                    List<Asignacion> asignacionFiltrado = docente.getAsignaciones()
                            .stream()
                            .filter(asi -> asi.getEnabled() &&
                                    asi.getCarga().getIdCarga().equals(idCarga))  // <-- nuevo filtro
                            .toList();

                    docente.setAsignaciones(asignacionFiltrado);

                    return convDocenteAsignacion(docente);
                })
                .toList();

        return new BaseListReponse<>(200, Modulo.DOCENTE.listado(), response);
    }



    private DocenteDetalleResponse convDocenteDetalle(Docente obj) {
        return modelMapper.map(obj, DocenteDetalleResponse.class);
    }
    private DocenteAsignacionResponse convDocenteAsignacion(Docente obj) {
        return modelMapper.map(obj, DocenteAsignacionResponse.class);
    }
    private DocenteEspecializacionResponse convDocenteEspecializacion(Docente obj){
        return modelMapper.map(obj, DocenteEspecializacionResponse.class);
    }
    private DocentePreferenciaResponse convDocentePreferencia(Docente obj){
        return modelMapper.map(obj, DocentePreferenciaResponse.class);
    }
    private DocenteDisponibilidadResponse convDocenteDisponibilidad(Docente obj){
        return modelMapper.map(obj, DocenteDisponibilidadResponse.class);
    }


}
