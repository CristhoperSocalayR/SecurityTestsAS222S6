package edu.pe.vallegrande.Security.service;

import com.google.firebase.auth.*;
import edu.pe.vallegrande.Security.dto.LoginDto;
import edu.pe.vallegrande.Security.dto.RegistroDto;
import edu.pe.vallegrande.Security.dto.TokenDto;
import edu.pe.vallegrande.Security.model.Rol;
import edu.pe.vallegrande.Security.model.Usuario;
import edu.pe.vallegrande.Security.repository.RolRepository;
import edu.pe.vallegrande.Security.repository.UsuarioRepository;
import edu.pe.vallegrande.Security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final JwtUtil jwtUtil;

    public Mono<TokenDto> registro(RegistroDto registroDto) {
        return usuarioRepository.count()
                .flatMap(count -> {
                    try {
                        // Crear usuario en Firebase
                        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                                .setEmail(registroDto.getEmail())
                                .setPassword(registroDto.getPassword())
                                .setDisplayName(registroDto.getNombre())
                                .setEmailVerified(false)
                                .setDisabled(false);

                        UserRecord userRecord = firebaseAuth.createUser(request);

                        // Determinar el rol basado en si es el primer usuario
                        String rolNombre = count == 0 ? "ADMIN" : "USER";

                        // Asignar claims al usuario en Firebase
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("rol", rolNombre);
                        firebaseAuth.setCustomUserClaims(userRecord.getUid(), claims);

                        // Crear usuario en la base de datos
                        Usuario usuario = Usuario.builder()
                                .email(registroDto.getEmail())
                                .nombre(registroDto.getNombre())
                                .uid(userRecord.getUid())
                                .identificador(userRecord.getUid())
                                .proveedores("password")
                                .fechaCreacion(LocalDateTime.now())
                                .fechaAcceso(LocalDateTime.now())
                                .activo(true)
                                .build();

                        return usuarioRepository.save(usuario)
                                .flatMap(usuarioGuardado -> {
                                    // Guardar rol
                                    Rol rol = Rol.builder()
                                            .uid(usuarioGuardado.getUid())
                                            .nombreRol(rolNombre)
                                            .build();

                                    return rolRepository.save(rol)
                                            .map(rolGuardado -> {
                                                // Generar token JWT
                                                String token = jwtUtil.generarToken(usuarioGuardado, rolNombre);

                                                return TokenDto.builder()
                                                        .token(token)
                                                        .uid(usuarioGuardado.getUid())
                                                        .email(usuarioGuardado.getEmail())
                                                        .rol(rolNombre)
                                                        .fechaAcceso(usuarioGuardado.getFechaAcceso())
                                                        .mensaje("Usuario registrado exitosamente")
                                                        .build();
                                            });
                                });
                    } catch (FirebaseAuthException e) {
                        log.error("Error al crear usuario en Firebase: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Error al registrar usuario: " + e.getMessage()));
                    }
                });
    }

    public Mono<TokenDto> login(LoginDto loginDto) {
        try {
            // Autenticar con Firebase
            UserRecord userRecord = firebaseAuth.getUserByEmail(loginDto.getEmail());

            // Verificar si el usuario existe en nuestra base de datos
            return usuarioRepository.findByUid(userRecord.getUid())
                    .switchIfEmpty(Mono.error(new RuntimeException("Cree su cuenta para poder iniciar sesión")))
                    .flatMap(usuario -> {
                        // Actualizar fecha de acceso
                        usuario.setFechaAcceso(LocalDateTime.now());
                        return usuarioRepository.save(usuario);
                    })
                    .flatMap(usuario -> rolRepository.findByUid(usuario.getUid())
                            .map(rol -> {
                                // Generar token JWT
                                String token = jwtUtil.generarToken(usuario, rol.getNombreRol());

                                return TokenDto.builder()
                                        .token(token)
                                        .uid(usuario.getUid())
                                        .email(usuario.getEmail())
                                        .rol(rol.getNombreRol())
                                        .fechaAcceso(usuario.getFechaAcceso())
                                        .mensaje("Ingreso con éxito")
                                        .build();
                            }));
        } catch (FirebaseAuthException e) {
            log.error("Error al autenticar usuario en Firebase: {}", e.getMessage());
            return Mono.error(new RuntimeException("Cree su cuenta para poder iniciar sesión"));
        }
    }
}
