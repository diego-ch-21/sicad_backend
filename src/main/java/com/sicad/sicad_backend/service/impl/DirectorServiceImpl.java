package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDirectorRepo;
import com.sicad.sicad_backend.repository.interfaces.IEscuelaRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static com.sicad.sicad_backend.Enum.Message.CORREO_EN_USO;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl
        extends CRUDImpl<Director, Integer>
        implements IDirectorService {

    private final IUsuarioRepo userRepository;
    private final IDirectorRepo directorRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IEscuelaRepo escuelaRepo;
    private final IRolRepo rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Director, Integer> getRepo() {
        return directorRepo;
    }

    @Override
    public BaseListReponse<DirectorDetalleResponse> listar() {
        List<DirectorDetalleResponse> lista = directorRepo.findByEnabledTrue()
                .stream()
                .map(this::convDirectorDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.DIRECTOR.listado(), lista);
    }

    @Override
    public BaseObjectResponse<DirectorDetalleResponse> buscar(Integer idDirector) {
        Optional<Director> directorOpt = directorRepo.findByIdAndEnabledTrue(idDirector);

        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404,Modulo.DIRECTOR.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.DIRECTOR.encontrado(),convDirectorDetalle(directorOpt.get()));
    }

    @Override
    public BaseObjectResponse<DirectorDetalleResponse> registrar(DirectorCreateRequest request) {
        Integer idRol = 2;
        if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
            return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
        }
        Optional<Rol> optionalRol = rolRepository.findById(idRol);
        if (!optionalRol.isPresent()) {
            return new BaseObjectResponse<>(404, Modulo.ROL.noEncontrado(), null);
        }
        Rol rolUsuario = optionalRol.get();
        String codigo;
        do {
            codigo = CodigoGeneratorUtil.generarCodigoNumerico(8);
        } while (userRepository.existsByCodigo(codigo));

        LocalDate fecha = LocalDate.now();
        Usuario usuario = Usuario.builder()
                .codigo(codigo)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .enabled(true)
                .cretedAt(fecha)
                .rol(rolUsuario)
                .build();
        usuarioRepo.save(usuario);

        String codigoDirector;
        do {
            codigoDirector = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (directorRepo.existsByCodigo(codigoDirector));

        Optional<Escuela> escuelaObj= escuelaRepo.findById(request.getIdEscuela());
        if(!escuelaObj.isPresent()){
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }

        Director director = Director.builder()
                .usuario(usuario)
                .escuela(escuelaObj.get())
                .enabled(true)
                .codigo(codigoDirector)
                .build();
        directorRepo.save(director);

        DirectorDetalleResponse response = convDirectorDetalle(director);

        return new BaseObjectResponse<>(201, Modulo.DIRECTOR.registrado(), response);
    }

    /*
    @Override
    public BaseListReponse<DirectorDetalleResponse> registrarAll(List<DirectorCreateRequest> requests) {
        List<DirectorDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (DirectorCreateRequest request : requests) {
            try {
                BaseObjectResponse<DirectorDetalleResponse> response = registrar(request);
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
        String mensaje = String.format("Cursos registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new BaseListReponse<>(201, mensaje,registrados);
    }

     */

    @Override
    public BaseObjectResponse<DirectorDetalleResponse> actualizar(Integer idDirector, DirectorUpdateRequest request) {
        Optional<Director> directorOpt = directorRepo.findByIdAndEnabledTrue(idDirector);
        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }
        Director director = directorOpt.get();

        Usuario usuario = director.getUsuario();

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepo.findByEmail(request.getEmail()).isPresent()) {
                return new BaseObjectResponse<>(409, CORREO_EN_USO.toString(), null);
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepo.save(usuario);

        if(request.getIdEscuela() != null){
            Optional<Escuela> escuelaObj= escuelaRepo.findById(request.getIdEscuela());
            if(!escuelaObj.isPresent()){
                return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
            } else {
                director.setEscuela(escuelaObj.get());
            }

        }

        directorRepo.save(director);
        DirectorDetalleResponse response = convDirectorDetalle(director);
        return new BaseObjectResponse<>(200, Modulo.DIRECTOR.actualizado(), response);
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idDirector) {
        Optional<Director> directorOpt = directorRepo.findByIdAndEnabledTrue(idDirector);
        if (directorOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, "Director no encontrada", null);
        }
        Director director = directorOpt.get();
        director.setEnabled(false);
        directorRepo.save(director);
        return new BaseObjectResponse<>(200, "se elimino el director exitosamente", null);
    }
    private DirectorDetalleResponse convDirectorDetalle(Director obj) {
        return modelMapper.map(obj, DirectorDetalleResponse.class);
    }
}
