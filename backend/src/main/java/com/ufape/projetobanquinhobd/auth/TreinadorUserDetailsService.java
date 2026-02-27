package com.ufape.projetobanquinhobd.auth;

import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreinadorUserDetailsService implements UserDetailsService {

    public static final String ADMIN_EMAIL = "admin@pokemon.com";

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Treinador treinador = treinadorRepository
                .findByCredenciaisEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Treinador não encontrado com e-mail: " + email));

        List<SimpleGrantedAuthority> authorities = ADMIN_EMAIL.equalsIgnoreCase(treinador.getCredenciais().getEmail())
                ? List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_TREINADOR")
                )
                : List.of(new SimpleGrantedAuthority("ROLE_TREINADOR"));

        return new User(
                treinador.getCredenciais().getEmail(),
                treinador.getCredenciais().getSenha(),
                authorities
        );
    }
}
