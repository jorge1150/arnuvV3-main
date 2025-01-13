package com.core.arnuv.services.imp;

import com.core.arnuv.model.Usuariodetalle;
import com.core.arnuv.repository.IUsuarioDetalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServicesAuth implements UserDetailsService {
    private final IUsuarioDetalleRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = repo.buscarPorEmailOrUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if(usuario.getEstado() != null && usuario.getEstado()){
            return new User(usuario.getUsername(), usuario.getPassword(), getAuthorities(usuario));
        }
        throw new LockedException("Usuario actualmente deshabilitado");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuariodetalle usuario) {
        return usuario.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }
}
