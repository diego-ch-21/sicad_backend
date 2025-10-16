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
import com.sicad.sicad_backend.jwt.JwtService;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.service.interfaces.IEscuelaProfesionalService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements IAuthService{
    private final IUsuarioRepo userRepo;
    private final IDocenteRepo docenteRepository;
    private final IDepartamentoAcademicoRepo departamentoAcademicoRepo;
    private final IEscuelaProfesionalRepo escuelaProfesionalRepo;
    private final ILogisticaRepo logisticaRepo;
    private final IRolRepo rolRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public BaseObjectResponse<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            Optional<Usuario> optionalUser = userRepo.findByEmailAndEnabledTrue(request.getEmail());
            if (optionalUser.isEmpty()) {
                return new BaseObjectResponse<>(404, CORREO_NO_ENCONTRADO.toString(), null);
            }
            Usuario usuario = optionalUser.get();
            String token = jwtService.getToken(usuario);
            //data usuario
            UsuarioDetalleResponse usuarioDetalleResponse = convUsuarioDetalle(usuario);
            Integer idRol = usuario.getRol().getIdRol();

            Docente docente=null;
            DepartamentoAcademico departamentoAcademico = null;
            Logistica logistica = null;
            EscuelaProfesional escuelaProfesional = null;
            RolEnum rol = RolEnum.obtener(idRol);
            if(rol ==null){
                return new BaseObjectResponse<>(404, "Usuario no cuenta con un rol", null);
            }
            switch (rol){
                case ADMIN: // Admin
                    break;
                case DEPARTAMENTO_ACADEMICO: // Departamento academico
                    departamentoAcademico = departamentoAcademicoRepo.findByUsuario(usuario).orElse(null);
                    break;
                case DOCENTE: // Docente
                    docente = docenteRepository.findByUsuario(usuario).orElse(null);
                    break;
                case ESCUELA_PROFESIONAL: //Escuela profesional
                    escuelaProfesional = escuelaProfesionalRepo.findByUsuario(usuario).orElse(null);
                    break;
                case LOGISTICA: //logistica
                    logistica = logisticaRepo.findByUsuario(usuario).orElse(null);
                    break;

            }

            DocenteUsuarioResponse docenteUsuarioResponse=null;
            if(docente != null){
                docenteUsuarioResponse = convDocenteUsuario(docente);
            }

            DepartamentoAcademicoUsuarioResponse departamentoAcademicoUsuarioResponse=null;
            if(departamentoAcademico != null){
                departamentoAcademicoUsuarioResponse = convDepartamentoAcademicoUsuario(departamentoAcademico);
            }
            EscuelaProfesionalUsuarioResponse escuelaProfesionalUsuarioResponse=null;
            if(escuelaProfesional != null){
                escuelaProfesionalUsuarioResponse = convEscuelaProfesionalUsuario(escuelaProfesional);
            }

            LogisticaUsuarioResponse logisticaUsuarioResponse=null;
            if(logistica != null){
                logisticaUsuarioResponse = convLogisticaUsuario(logistica);
            }

            RolesResponse rolesResponse = RolesResponse.builder()
                    .docente(docenteUsuarioResponse)
                    .departamentoAcademico(departamentoAcademicoUsuarioResponse)
                    .escuelaProfesional(escuelaProfesionalUsuarioResponse)
                    .logistica(logisticaUsuarioResponse)
                    .build();


            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .usuario(usuarioDetalleResponse)
                    .roles(rolesResponse)
                    .build();

            return new BaseObjectResponse<>(200, LOGIN_ACCESS.toString(), authResponse);

        } catch (BadCredentialsException e) {
            return new BaseObjectResponse<>(401, CREDENCIALES_INCORRECTAS.toString(), null);
        }
    }

    //no se usara
    public BaseObjectResponse<AuthResponse> registerAdmin(RegisterRequest request) {
        Integer idRol =RolEnum.ADMIN.getNumero();
        Optional<Usuario> optionalUser = userRepo.findByEmailAndEnabledTrue(request.getEmail());
        if (optionalUser.isEmpty()) {
            return new BaseObjectResponse<>(404, CORREO_NO_ENCONTRADO.toString(), null);
        }
        Optional<Rol> optionalRol = rolRepo.findById(idRol);
        if (!optionalRol.isPresent()) {
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }
        Rol rolUsuario = optionalRol.get();

        // Generar código único y verificar duplicado
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (userRepo.existsByCodigo(codigo));

        LocalDate createdAt = LocalDate.now();

        // Crear y guardar usuario
        Usuario user = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(createdAt)
                .rol(rolUsuario)
                .build();

        userRepo.save(user);

        // Generar token y respuesta
        UsuarioDetalleResponse usuarioDetalleResponse = convUsuarioDetalle(user);
        String token = jwtService.getToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .usuario(usuarioDetalleResponse)
                .build();

        return new BaseObjectResponse<>(201, Modulo.USUARIO.registrado(), authResponse);
    }



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
