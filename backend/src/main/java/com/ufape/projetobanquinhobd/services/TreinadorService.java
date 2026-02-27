package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.CredenciaisUsuario;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreinadorService {

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Treinador salvar(Treinador treinador) {
        CredenciaisUsuario creds = treinador.getCredenciais();
        String senha = creds.getSenha();

        if (!senha.startsWith("$2")) {
            String senhaCriptografada = passwordEncoder.encode(senha);
            CredenciaisUsuario credenciaisCriptografadas =
                    new CredenciaisUsuario(creds.getEmail(), senhaCriptografada);
            treinador.setCredenciais(credenciaisCriptografadas);
        }

        return treinadorRepository.save(treinador);
    }

    public List<Treinador> listarTodos() {
        return treinadorRepository.findAll();
    }

    public Optional<Treinador> buscarPorId(Long id) {
        return treinadorRepository.findById(id);
    }

    public void deletarPorId(Long id) {
        treinadorRepository.deleteById(id);
    }
}
