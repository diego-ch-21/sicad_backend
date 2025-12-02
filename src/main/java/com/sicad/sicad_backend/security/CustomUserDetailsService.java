package com.sicad.sicad_backend.security;

import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//clase 2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
    implements UserDetailsService {

    private final IUsuarioRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = repo.findByEmailAndEnabledTrue(username);
        if(usuarioOpt.isEmpty()){
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        Usuario usuario = usuarioOpt.get();
        List<GrantedAuthority> roles = new ArrayList<>();
        String rol = usuario.getRol().getNombre();
        roles.add(new SimpleGrantedAuthority(rol));
        return new User(usuario.getEmail(),usuario.getPassword(),roles);
    }
}
