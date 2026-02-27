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

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Treinador treinador = treinadorRepository
                .findByCredenciaisEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Treinador não encontrado com e-mail: " + email));

        return new User(
                treinador.getCredenciais().getEmail(),
                treinador.getCredenciais().getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_TREINADOR"))
        );
    }
}
