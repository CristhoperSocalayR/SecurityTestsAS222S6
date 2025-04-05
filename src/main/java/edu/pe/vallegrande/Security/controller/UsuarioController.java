package edu.pe.vallegrande.Security.controller;

import edu.pe.vallegrande.Security.model.Usuario;
import edu.pe.vallegrande.Security.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Flux<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{uid}")
    public Mono<ResponseEntity<Usuario>> obtenerUsuarioPorUid(@PathVariable String uid) {
        return usuarioService.obtenerUsuarioPorUid(uid)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Map<String, String>>> eliminarUsuario(@PathVariable String uid) {
        return usuarioService.eliminarUsuario(uid)
                .then(Mono.just(new ResponseEntity<>(
                        Map.of("mensaje", "Usuario eliminado correctamente"),
                        HttpStatus.OK
                )))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(
                        Map.of("error", e.getMessage()),
                        HttpStatus.BAD_REQUEST
                )));
    }
}