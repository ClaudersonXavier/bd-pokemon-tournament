package com.ufape.projetobanquinhobd.auth;

import com.ufape.projetobanquinhobd.auth.dto.LoginRequest;
import com.ufape.projetobanquinhobd.auth.dto.LoginResponse;
import com.ufape.projetobanquinhobd.auth.dto.RegistroRequest;
import com.ufape.projetobanquinhobd.entities.CredenciaisUsuario;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TreinadorUserDetailsService userDetailsService;

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // POST /api/auth/login
    // Autentica o treinador com e-mail e senha e devolve um token JWT.

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "E-mail ou senha incorretos"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Treinador treinador = treinadorRepository
                .findByCredenciaisEmail(request.getEmail())
                .orElseThrow();

        Map<String, Object> claimsExtras = Map.of(
                "id", treinador.getId(),
                "nome", treinador.getNome()
        );

        String token = jwtService.gerarToken(claimsExtras, userDetails);

        return ResponseEntity.ok(new LoginResponse(
                token, "Bearer",
                treinador.getId(), treinador.getNome(),
                treinador.getCredenciais().getEmail()
        ));
    }

    // POST /api/auth/registro
    // Cadastra um novo treinador e devolve um token JWT.

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {
        if (treinadorRepository.findByCredenciaisEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(409)
                    .body(Map.of("erro", "E-mail já cadastrado"));
        }

        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        CredenciaisUsuario credenciais = new CredenciaisUsuario(request.getEmail(), senhaCriptografada);
        Treinador treinador = new Treinador(request.getNome(), credenciais);
        treinador = treinadorRepository.save(treinador);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Map<String, Object> claimsExtras = Map.of(
                "id", treinador.getId(),
                "nome", treinador.getNome()
        );

        String token = jwtService.gerarToken(claimsExtras, userDetails);

        return ResponseEntity.status(201).body(new LoginResponse(
                token, "Bearer",
                treinador.getId(), treinador.getNome(),
                treinador.getCredenciais().getEmail()
        ));
    }
}
