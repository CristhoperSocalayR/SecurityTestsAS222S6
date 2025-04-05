package edu.pe.vallegrande.Security.service;

import edu.pe.vallegrande.Security.model.Usuario;
import edu.pe.vallegrande.Security.repository.RolRepository;
import edu.pe.vallegrande.Security.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FirebaseAuth firebaseAuth;

    public Flux<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Mono<Usuario> obtenerUsuarioPorUid(String uid) {
        return usuarioRepository.findByUid(uid);
    }

    public Mono<Void> eliminarUsuario(String uid) {
        // Primero verificamos que exista el usuario
        return usuarioRepository.findByUid(uid)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    try {
                        // Eliminar de Firebase
                        firebaseAuth.deleteUser(uid);

                        // Eliminar roles asociados
                        return rolRepository.findByUid(uid)
                                .flatMap(rol -> rolRepository.delete(rol))
                                .then(usuarioRepository.delete(usuario));
                    } catch (FirebaseAuthException e) {
                        log.error("Error al eliminar usuario en Firebase: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error al eliminar usuario: " + e.getMessage()));
                    }
                });
    }
}