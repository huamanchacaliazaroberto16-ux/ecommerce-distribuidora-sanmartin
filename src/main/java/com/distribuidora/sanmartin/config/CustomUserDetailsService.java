package com.distribuidora.sanmartin.config;

import com.distribuidora.sanmartin.models.Usuario;
import com.distribuidora.sanmartin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        String rol = usuario.getId_rol() == 1 ? "ROLE_ADMIN" : "ROLE_CLIENTE";
        return new org.springframework.security.core.userdetails.User(
            usuario.getUsername(),
            usuario.getPassword_hash(),
            List.of(new SimpleGrantedAuthority(rol))
        );
    }
}