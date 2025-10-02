package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.docente.*;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl extends CRUDImpl<Docente, Integer> implements IDocenteService {

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

    public GenericObjectResponse<DocenteDetalleResponse> registrarDocente(DocenteCreateRequest request) {

        // 1. Verificar si el email ya está registrado
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
        }

        // 2. Buscar entidades relacionadas
        Rol rol = rolRepo.findById(3).orElse(null); // Suponiendo que 3 = DOCENTE
        if (rol == null)
            return new GenericObjectResponse<>(404, "Rol Docente no encontrado", null);

        Dedicacion dedicacion = dedicacionRepo.findById(request.getIdDedicacion()).orElse(null);
        if (dedicacion == null)
            return new GenericObjectResponse<>(404, "Dedicación no encontrada", null);

        Categoria categoria = categoriaRepo.findById(request.getIdCategoria()).orElse(null);
        if (categoria == null)
            return new GenericObjectResponse<>(404, "Categoría no encontrada", null);

        // 3. Generar código único
        String codigoUsuario;
        do {
            codigoUsuario = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (usuarioRepo.existsByCodigo(codigoUsuario));

        // 5. Crear y guardar Docente
        LocalDate fecha = LocalDate.now();

        // 4. Crear y guardar Usuario
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
            codigoDocente = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (docenteRepo.existsByCodigo(codigoDocente));


        Docente docente = Docente.builder()
                .usuario(usuario)
                .dedicacion(dedicacion)
                .categoria(categoria)
                .codigo(codigoDocente)
                .enabled(true)
                .build();
        docenteRepo.save(docente);

        // 6. Mapear y retornar
        DocenteDetalleResponse docenteDTO = modelMapper.map(docente, DocenteDetalleResponse.class);

        return new GenericObjectResponse<>(201, "Docente registrado exitosamente", docenteDTO);
    }
    public GenericObjectResponse<DocenteDetalleResponse> actualizarDocente(Integer idDocente, DocenteUpdateRequest request) {

        // 1. Verificar existencia del docente
        Docente docente = docenteRepo.findById(idDocente).orElse(null);
        if (docente == null) {
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }

        // 2. Validar email si ha cambiado
        if (!docente.getUsuario().getEmail().equals(request.getEmail()) &&
                usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
        }

        // 3. Buscar entidades relacionadas
        Dedicacion dedicacion = dedicacionRepo.findById(request.getIdDedicacion()).orElse(null);
        if (dedicacion == null)
            return new GenericObjectResponse<>(404, "Dedicación no encontrada", null);

        Categoria categoria = categoriaRepo.findById(request.getIdCategoria()).orElse(null);
        if (categoria == null)
            return new GenericObjectResponse<>(404, "Categoría no encontrada", null);

        // 4. Actualizar datos del Usuario
        Usuario usuario = docente.getUsuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuarioRepo.save(usuario);

        // 5. Actualizar datos del Docente
        docente.setDedicacion(dedicacion);
        docente.setCategoria(categoria);
        docenteRepo.save(docente);

        // 6. Mapear y retornar
        DocenteDetalleResponse docenteDTO = modelMapper.map(docente, DocenteDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Docente actualizado exitosamente", docenteDTO);
    }
    //service para obtener un docente por usuario
    public GenericObjectResponse<DocenteDetalleResponse> obtenerDocentePorUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> null);
        if(usuario == null) {
            return new GenericObjectResponse<>(404, "Usuario no encontrado", null);
        }
        if(usuario.getRol().getIdRol() != 3) {
            return new GenericObjectResponse<>(404, "El usuario no es un docente", null);

        }
        Docente docente = docenteRepo.findByUsuario(usuario)
                .orElseThrow(() ->(null));
        if(docente == null) {
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }
        DocenteDetalleResponse docenteDTO = modelMapper.map(docente, DocenteDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Docente encontrado exitosamente", docenteDTO);
    }
    private DocenteDetalleResponse registrarDocenteInterno(DocenteCreateRequest request) {

        // Validación: correo existente
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return null; // ya existe
        }

        // Relacionados
        Rol rol = rolRepo.findById(3).orElse(null);
        Dedicacion dedicacion = dedicacionRepo.findById(request.getIdDedicacion()).orElse(null);
        Categoria categoria = categoriaRepo.findById(request.getIdCategoria()).orElse(null);
        if (rol == null || dedicacion == null || categoria == null) {
            return null;
        }

        // Código único usuario
        String codigoUsuario;
        do {
            codigoUsuario = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (usuarioRepo.existsByCodigo(codigoUsuario));

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .codigo(codigoUsuario)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(LocalDate.now())
                .rol(rol)
                .build();
        usuarioRepo.save(usuario);


        // Código docente
        String codigoDocente;
        do {
            codigoDocente = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (docenteRepo.existsByCodigo(codigoDocente));

        Docente docente = Docente.builder()
                .usuario(usuario)
                .dedicacion(dedicacion)
                .categoria(categoria)
                .codigo(codigoDocente)
                .enabled(true)
                .build();
        docenteRepo.save(docente);

        return modelMapper.map(docente, DocenteDetalleResponse.class);
    }


    public GenericReponse<DocenteDetalleResponse> registrarDocentes(List<DocenteCreateRequest> requestList) {
        List<DocenteDetalleResponse> registrados = requestList.stream()
                .map(this::registrarDocenteInterno)
                .filter(dto -> dto != null)
                .toList();

        int total = requestList.size();
        int exitosos = registrados.size();
        int fallidos = total - exitosos;

        String mensaje;
        if (exitosos == 0) {
            mensaje = "No se registró ningún docente. Todos los registros fallaron.";
            return new GenericReponse<>(409, mensaje, null);
        } else if (fallidos == 0) {
            mensaje =  exitosos + "docentes registrados exitosamente.";
        } else {
            mensaje = exitosos + " docentes registrados exitosamente. " + fallidos + " registros fallaron (posible correo duplicado o datos inválidos).";
        }

        return new GenericReponse<>(201, mensaje, registrados);
    }

    public GenericReponse<DocenteEspecializacionResponse> listarDocentesConEspecializaciones() {
        List<Docente> docentes = docenteRepo.findAllWithDocentesEspecializacion(); // trae docentes + especializaciones

        if (docentes.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron docentes", null);
        }

        // Convertir a DTOs
        List<DocenteEspecializacionResponse> listaDTO = docentes.stream()
                .map(this::convertToDocenteEspecializacion)
                .collect(Collectors.toList());
        return new GenericReponse<>(200, "Lista de docentes con especializaciones", listaDTO);
    }

    public GenericReponse<DocentePreferenciaResponse> listarDocentesConPreferencias(Integer idCicloAcademico) {
        if (idCicloAcademico == null) {
            return new GenericReponse<>(400, "idCicloAcademico no proporcionado", null);
        }

        List<Docente> docentes = docenteRepo.findAllWithDocentesPreferencia(); // trae docentes + preferencias

        if(docentes.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron docentes", null);
        }

        List<DocentePreferenciaResponse> responseList = docentes.stream()
                .map(docente -> {
                    // Mapear entidad Docente a DTO
                    DocentePreferenciaResponse dto = modelMapper.map(docente, DocentePreferenciaResponse.class);

                    // Filtrar preferencias por idCargaElectiva
                    List<PreferenciaResumenResponse> preferenciasFiltradas = docente.getPreferencias().stream()
                            .filter(pref -> pref.getCicloAcademico() != null &&
                                    pref.getCicloAcademico().getIdCicloAcademico().equals(idCicloAcademico))
                            .map(pref -> modelMapper.map(pref, PreferenciaResumenResponse.class))
                            .toList();

                    dto.setPreferencias(preferenciasFiltradas);

                    return dto;
                })
                .toList();

        return new GenericReponse<>(200, "Lista de docentes con preferencias", responseList);
    }

    public GenericReponse<DocenteDisponibilidadResponse> listarDocentesConDisponibilidad(Integer idCicloAcademico) {
        if (idCicloAcademico == null) {
            return new GenericReponse<>(400, "idCicloAcademico no proporcionado", null);
        }
        List<Docente> docentes = docenteRepo.findAllWithDocentesDisponibilidad(); // trae docentes + disponibilidad

        if(docentes.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron docentes", null);
        }

        List<DocenteDisponibilidadResponse> responseList = docentes.stream()
                .map(docente -> {
                    // Mapear entidad Docente a DTO
                    DocenteDisponibilidadResponse dto = modelMapper.map(docente, DocenteDisponibilidadResponse.class);

                    // Filtrar preferencias por idCargaElectiva
                    List<DisponibilidadResumenResponse> disponibilidadFiltradas = docente.getDisponibilidad().stream()
                            .filter(dis -> dis.getCicloAcademico() != null &&
                                    dis.getCicloAcademico().getIdCicloAcademico().equals(idCicloAcademico))
                            .map(dis -> modelMapper.map(dis, DisponibilidadResumenResponse.class))
                            .toList();

                    dto.setDisponibilidad(disponibilidadFiltradas);

                    return dto;
                })
                .toList();

        return new GenericReponse<>(200, "Lista de docentes con preferencias", responseList);
    }

    //solo docentes que tiene asigancione
    public GenericReponse<DocenteAsignacionResponse> listarDocentesCargaConAsignaciones(
            Integer idCarga) {
        if (idCarga == null) {
            return new GenericReponse<>(400, "idCarga no proporcionado", null);
        }

        Boolean isCarga = cargaRepo.existsByIdCarga(idCarga);
        if(!isCarga) {
            return new GenericReponse<>(200, "No se encontraron carga", null);
        }

        // Trae todos los docentes con sus asignaciones filtradas
        List<Docente> docentes = docenteRepo.findAllWithAsignacionesByCargaYCiclo(idCarga);

        if (docentes.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron docentes", null);
        }

        // Mapear entidades a DTOs usando modelMapper
        List<DocenteAsignacionResponse> lista = docentes.stream()
                .map(this::convertToAsignacionResponseDTO)
                .toList();

        return new GenericReponse<>(200, "Lista de docentes con asignaciones", lista);
    }

    public GenericObjectResponse<String> eliminarDocente(Integer idDocente) {
        // Validación de parámetro
        if (idDocente == null) {
            return new GenericObjectResponse<>(400, "idDocente no proporcionado", null);
        }

        // Validar existencia
        Docente docente = docenteRepo.findById(idDocente).orElse(null);
        if (docente == null) {
            return new GenericObjectResponse<>(404, "Docente  no encontrado", null);
        }

        // desabilitar
        docente.setEnabled(false);
        docenteRepo.save(docente);
        return new GenericObjectResponse<>(200, "se elimino el docente exitosamente", null);
    }

    private DocenteAsignacionResponse convertToAsignacionResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteAsignacionResponse.class);
    }
    private DocenteEspecializacionResponse convertToDocenteEspecializacion(Docente obj){
        return modelMapper.map(obj, DocenteEspecializacionResponse.class);
    }

    @Override
    public List<Docente> findByEnabledTrue() {
        return docenteRepo.findByEnabledTrue();

    }
}
