package edu.pe.vallegrande.Security.security;

import edu.pe.vallegrande.Security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            String uid = jwtUtil.extraerUidDelToken(token);
            String rol = jwtUtil.extraerRolDelToken(token);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    uid,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(rol))
            );

            return Mono.just(auth);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
