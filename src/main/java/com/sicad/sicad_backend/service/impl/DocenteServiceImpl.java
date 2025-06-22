package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.docente.DocenteRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResponseDTO;
import com.sicad.sicad_backend.dto.docente.DocenteUpdateRequestDTO;
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl extends CRUDImpl<Docente, Integer> implements IDocenteService {

    private final IDocenteRepo docenteRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IRolRepo rolRepo;
    private final ICategoriaRepo categoriaRepo;
    private final IDedicacionRepo dedicacionRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Docente, Integer> getRepo() {
        return docenteRepo;
    }

    public GenericObjectResponse<DocenteResponseDTO> registrarDocente(DocenteRequestDTO request) {

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
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(8);
        } while (usuarioRepo.existsByCodigo(codigo));

        // 4. Crear y guardar Usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .codigo(codigo)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .rol(rol)
                .build();
        usuarioRepo.save(usuario);

        // 5. Crear y guardar Docente
        Date fecha = Date.valueOf(ZonedDateTime.now().toLocalDate());

        Docente docente = Docente.builder()
                .usuario(usuario)
                .dedicacion(dedicacion)
                .categoria(categoria)
                .horasMaxLectivas(request.getHorasMaxLectivas())
                .tienePermisoExceso(request.getTienePermisoExceso())
                .createdAt(fecha)
                .enabled(true)
                .build();
        docenteRepo.save(docente);

        // 6. Mapear y retornar
        DocenteResponseDTO docenteDTO = modelMapper.map(docente, DocenteResponseDTO.class);

        return new GenericObjectResponse<>(201, "Docente registrado exitosamente", docenteDTO);
    }
    public GenericObjectResponse<DocenteResponseDTO> actualizarDocente(Integer idDocente, DocenteUpdateRequestDTO request) {

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
        docente.setHorasMaxLectivas(request.getHorasMaxLectivas());
        docente.setTienePermisoExceso(request.getTienePermisoExceso());
        docenteRepo.save(docente);

        // 6. Mapear y retornar
        DocenteResponseDTO docenteDTO = modelMapper.map(docente, DocenteResponseDTO.class);
        return new GenericObjectResponse<>(200, "Docente actualizado exitosamente", docenteDTO);
    }
    //service para obtener un docente por usuario
    public GenericObjectResponse<DocenteResponseDTO> obtenerDocentePorUsuario(Integer idUsuario) {
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
        DocenteResponseDTO docenteDTO = modelMapper.map(docente, DocenteResponseDTO.class);
        return new GenericObjectResponse<>(201, "Docente encontrado exitosamente", docenteDTO);
    }

    private DocenteResponseDTO convertToResponseDTO(Docente obj) {
        return modelMapper.map(obj, DocenteResponseDTO.class);
    }
    private DocenteRequestDTO convertToDTO(Docente obj) {
        return modelMapper.map(obj, DocenteRequestDTO.class);
    }

    private Docente convertToEntity(DocenteRequestDTO dto) {
        return modelMapper.map(dto, Docente.class);
    }
}
