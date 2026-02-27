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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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

    // GET /api/auth/test - Endpoint de teste
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "API funcionando", "timestamp", System.currentTimeMillis()));
    }


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
        boolean isAdmin = TreinadorUserDetailsService.ADMIN_EMAIL
                .equalsIgnoreCase(treinador.getCredenciais().getEmail());

        Map<String, Object> claimsExtras = Map.of(
                "id", treinador.getId(),
                "nome", treinador.getNome(),
                "admin", isAdmin
        );

        String token = jwtService.gerarToken(claimsExtras, userDetails);

        return ResponseEntity.ok(new LoginResponse(
                token, "Bearer",
                treinador.getId(), treinador.getNome(),
                treinador.getCredenciais().getEmail(),
                isAdmin
        ));
    }

    // POST /api/auth/registro
    // Cadastra um novo treinador e devolve um token JWT.

    @Transactional
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {
        if (TreinadorUserDetailsService.ADMIN_EMAIL.equalsIgnoreCase(request.getEmail())) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Este e-mail é reservado para a conta administrativa"));
        }

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
                "nome", treinador.getNome(),
                "admin", false
        );

        String token = jwtService.gerarToken(claimsExtras, userDetails);

        return ResponseEntity.status(201).body(new LoginResponse(
                token, "Bearer",
                treinador.getId(), treinador.getNome(),
                treinador.getCredenciais().getEmail(),
                false
        ));
    }

    // PUT /api/auth/change-password
    // Altera a senha do treinador autenticado.

    @Transactional
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Não autenticado"));
        }

        String email = auth.getName();
        String senhaAtual = body.get("senhaAtual");
        String novaSenha = body.get("novaSenha");

        if (senhaAtual == null || novaSenha == null || senhaAtual.isBlank() || novaSenha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "senhaAtual e novaSenha são obrigatórios"));
        }

        if (novaSenha.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A nova senha deve ter pelo menos 6 caracteres"));
        }

        Treinador treinador = treinadorRepository
                .findByCredenciaisEmail(email)
                .orElseThrow(() -> new RuntimeException("Treinador não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, treinador.getCredenciais().getSenha())) {
            return ResponseEntity.status(401).body(Map.of("erro", "Senha atual incorreta"));
        }

        String novaSenhaCriptografada = passwordEncoder.encode(novaSenha);
        treinador.setCredenciais(new CredenciaisUsuario(email, novaSenhaCriptografada));
        treinadorRepository.save(treinador);

        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso"));
    }

    // PUT /api/auth/update-nome
    // Atualiza o nome do treinador autenticado.
    
    @Transactional
    @PutMapping("/update-nome")
    public ResponseEntity<?> updateNome(@RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Não autenticado"));
        }

        String email = auth.getName();
        String novoNome = body.get("nome");

        if (novoNome == null || novoNome.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome é obrigatório"));
        }

        Treinador treinador = treinadorRepository
                .findByCredenciaisEmail(email)
                .orElseThrow(() -> new RuntimeException("Treinador não encontrado"));

        treinador.setNome(novoNome);
        treinadorRepository.save(treinador);

        return ResponseEntity.ok(Map.of("mensagem", "Nome atualizado com sucesso", "nome", novoNome));
    }
}
