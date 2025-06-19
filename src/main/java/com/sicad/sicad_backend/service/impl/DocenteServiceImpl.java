package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.auth.AuthResponse;
import com.sicad.sicad_backend.dto.DocenteDTO;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public GenericObjectResponse<AuthResponse> registrarDocente(insertarDocenteRequest request) {
        // 1. Validar email único
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new GenericObjectResponse<>(409, "El correo ya está en uso", null);
        }

        // 2. Validar existencia de rol, dedicación y categoría
        Rol rol = rolRepo.findById(3).orElse(null); // 3: Rol DOCENTE
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

        // 4. Crear Usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .codigo(codigo)
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .rol(rol)
                .enabled(true)
                .build();
        usuarioRepo.save(usuario);

        // 5. Crear Docente
        Docente docente = Docente.builder()
                .idUsuario(usuario)
                .dedicacion(dedicacion)
                .categoria(categoria)
                .horasMaxLectivas(request.getHorasMaxLectivas())
                .tienePermisoExceso(request.getTienePermisoExceso())
                .fechaCreacion(ZonedDateTime.now())
                .enabled(true)
                .build();
        docenteRepo.save(docente);

        // 6. Preparar respuesta
        String token = jwtService.getToken(usuario);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .usuario(modelMapper.map(usuario, UsuarioDTO.class))
                .docente(modelMapper.map(docente, DocenteDTO.class))
                .build();

        return new GenericObjectResponse<>(201, "Docente registrado exitosamente", authResponse);
    }

    private DocenteDTO convertToDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDTO.class);
    }

    private Docente convertToEntity(DocenteDTO dto) {
        return modelMapper.map(dto, Docente.class);
    }
}
