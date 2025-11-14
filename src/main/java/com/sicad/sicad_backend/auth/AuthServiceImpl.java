package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.Enum.RolEnum;
import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.auth.dto.RolesResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUsuarioResponse;
import com.sicad.sicad_backend.dto.docente.DocenteUsuarioResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUsuarioResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUsuarioResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.security.JwtTokenUtil;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepo userRepo;
    private final IDocenteRepo docenteRepository;
    private final IDepartamentoAcademicoRepo departamentoAcademicoRepo;
    private final IEscuelaProfesionalRepo escuelaProfesionalRepo;
    private final ILogisticaRepo logisticaRepo;
    private final IRolRepo rolRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    /**
     * ✅ Método de autenticación privado mejorado
     */
    private void authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException e) {
            log.error("Usuario deshabilitado: {}", request.getEmail());
            throw new DisabledException("Usuario deshabilitado", e);
        } catch (BadCredentialsException e) {
            log.error("Credenciales inválidas para: {}", request.getEmail());
            throw new BadCredentialsException("Credenciales inválidas", e);
        }
    }

    /**
     * ✅ Login mejorado con mejor manejo de errores
     */
    @Override
    public BaseObjectResponse<AuthResponse> login(LoginRequest request) {

        // 1. Autenticar usuario
        authenticate(request);

        // 2. Cargar detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // 3. Generar token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // 4. Buscar usuario en la base de datos
        Optional<Usuario> optionalUser = userRepo.findByEmailAndEnabledTrue(request.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("Usuario no encontrado después de autenticación: {}", request.getEmail());
            return new BaseObjectResponse<>(404, CORREO_NO_ENCONTRADO.toString(), null);
        }

        Usuario usuario = optionalUser.get();
        UsuarioDetalleResponse usuarioDetalleResponse = convUsuarioDetalle(usuario);

        // 5. Validar rol del usuario
        Integer idRol = usuario.getRol().getIdRol();
        RolEnum rol = RolEnum.obtener(idRol);

        if (rol == null) {
            log.error("Rol inválido para usuario: {} - idRol: {}", request.getEmail(), idRol);
            return new BaseObjectResponse<>(404, "Usuario no cuenta con un rol válido", null);
        }

        // 6. Obtener información específica según el rol
        RolesResponse rolesResponse = obtenerInformacionPorRol(usuario, rol);

        // 7. Construir respuesta
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .usuario(usuarioDetalleResponse)
                .roles(rolesResponse)
                .build();

        log.info("Login exitoso para usuario: {}", request.getEmail());
        return new BaseObjectResponse<>(200, LOGIN_ACCESS.toString(), authResponse);
    }

    /**
     * ✅ Método extraído para mejor legibilidad - Obtiene información según el rol
     */
    private RolesResponse obtenerInformacionPorRol(Usuario usuario, RolEnum rol) {
        DocenteUsuarioResponse docenteUsuarioResponse = null;
        DepartamentoAcademicoUsuarioResponse departamentoAcademicoUsuarioResponse = null;
        EscuelaProfesionalUsuarioResponse escuelaProfesionalUsuarioResponse = null;
        LogisticaUsuarioResponse logisticaUsuarioResponse = null;

        switch (rol) {
            case ADMIN:
                // Admin no requiere información adicional
                break;

            case DEPARTAMENTO_ACADEMICO:
                departamentoAcademicoRepo.findByUsuario(usuario)
                        .ifPresent(dept -> {
                            // Se asigna a variable de método para usar en builder
                        });
                DepartamentoAcademico departamentoAcademico =
                        departamentoAcademicoRepo.findByUsuario(usuario).orElse(null);
                if (departamentoAcademico != null) {
                    departamentoAcademicoUsuarioResponse = convDepartamentoAcademicoUsuario(departamentoAcademico);
                }
                break;

            case DOCENTE:
                Docente docente = docenteRepository.findByUsuarioAndEnabledTrue(usuario).orElse(null);
                if (docente != null) {
                    docenteUsuarioResponse = convDocenteUsuario(docente);
                }
                break;

            case ESCUELA_PROFESIONAL:
                EscuelaProfesional escuelaProfesional =
                        escuelaProfesionalRepo.findByUsuario(usuario).orElse(null);
                if (escuelaProfesional != null) {
                    escuelaProfesionalUsuarioResponse = convEscuelaProfesionalUsuario(escuelaProfesional);
                }
                break;

            case LOGISTICA:
                Logistica logistica = logisticaRepo.findByUsuario(usuario).orElse(null);
                if (logistica != null) {
                    logisticaUsuarioResponse = convLogisticaUsuario(logistica);
                }
                break;
        }

        return RolesResponse.builder()
                .docente(docenteUsuarioResponse)
                .departamentoAcademico(departamentoAcademicoUsuarioResponse)
                .escuelaProfesional(escuelaProfesionalUsuarioResponse)
                .logistica(logisticaUsuarioResponse)
                .build();
    }

    /**
     * ✅ Registro de admin corregido (usar solo si es necesario)
     */
    @Override
    public BaseObjectResponse<AuthResponse> registerAdmin(RegisterRequest request) {
        // 1. Verificar si el correo ya está registrado
        if (userRepo.findByEmailAndEnabledTrue(request.getEmail()).isPresent()) {
            log.warn("Intento de registro con correo duplicado: {}", request.getEmail());
            return new BaseObjectResponse<>(409, "El correo ya está registrado", null);
        }

        // 2. Buscar el rol de administrador
        Integer idRol = RolEnum.ADMIN.getNumero();
        Optional<Rol> rolOpt = rolRepo.findByIdAndEnabledTrue(idRol);

        if (rolOpt.isEmpty()) {
            log.error("Rol de administrador no encontrado en la base de datos");
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }

        Rol rolUsuario = rolOpt.get();

        // 3. Generar código único
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (userRepo.existsByCodigo(codigo));

        // 4. Crear usuario
        Usuario user = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(null) // ✅ Incluir apellido si existe en request
                .enabled(true)
                .cretedAt(LocalDate.now())
                .rol(rolUsuario)
                .build();

        userRepo.save(user);
        log.info("Administrador registrado exitosamente: {}", request.getEmail());

        // 5. Generar token y respuesta
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenUtil.generateToken(userDetails);

        UsuarioDetalleResponse usuarioDetalleResponse = convUsuarioDetalle(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .usuario(usuarioDetalleResponse)
                .roles(null) // Admin no tiene roles adicionales
                .build();

        return new BaseObjectResponse<>(201, Modulo.USUARIO.registrado(), authResponse);
    }

    // ========================================
    // MÉTODOS DE CONVERSIÓN
    // ========================================

    private UsuarioDetalleResponse convUsuarioDetalle(Usuario obj) {
        return modelMapper.map(obj, UsuarioDetalleResponse.class);
    }

    private DocenteUsuarioResponse convDocenteUsuario(Docente obj) {
        return modelMapper.map(obj, DocenteUsuarioResponse.class);
    }

    private DepartamentoAcademicoUsuarioResponse convDepartamentoAcademicoUsuario(DepartamentoAcademico obj) {
        return modelMapper.map(obj, DepartamentoAcademicoUsuarioResponse.class);
    }

    private EscuelaProfesionalUsuarioResponse convEscuelaProfesionalUsuario(EscuelaProfesional obj) {
        return modelMapper.map(obj, EscuelaProfesionalUsuarioResponse.class);
    }

    private LogisticaUsuarioResponse convLogisticaUsuario(Logistica obj) {
        return modelMapper.map(obj, LogisticaUsuarioResponse.class);
    }
}