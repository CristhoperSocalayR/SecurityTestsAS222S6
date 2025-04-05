package edu.pe.vallegrande.Security.repository;

import edu.pe.vallegrande.Security.model.Usuario;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long> {
    Mono<Usuario> findByUid(String uid);
    Mono<Usuario> findByEmail(String email);
    Mono<Long> count();
}