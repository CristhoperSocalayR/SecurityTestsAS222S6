package edu.pe.vallegrande.Security.repository;

import edu.pe.vallegrande.Security.model.Rol;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RolRepository extends ReactiveCrudRepository<Rol, Long> {
    Mono<Rol> findByUid(String uid);

    @Query("SELECT r.* FROM roles r WHERE r.uid = :uid AND r.nombre_rol = :nombreRol")
    Mono<Rol> findByUidAndNombreRol(String uid, String nombreRol);
}