package edu.pe.vallegrande.Security.controller;


import edu.pe.vallegrande.Security.dto.LoginDto;
import edu.pe.vallegrande.Security.dto.RegistroDto;
import edu.pe.vallegrande.Security.dto.TokenDto;
import edu.pe.vallegrande.Security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public Mono<ResponseEntity<TokenDto>> registro(@RequestBody RegistroDto registroDto) {
        return authService.registro(registroDto)
                .map(tokenDto -> new ResponseEntity<>(tokenDto, HttpStatus.CREATED))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(
                        TokenDto.builder().mensaje(e.getMessage()).build(),
                        HttpStatus.BAD_REQUEST
                )));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenDto>> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto)
                .map(tokenDto -> new ResponseEntity<>(tokenDto, HttpStatus.OK))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(
                        TokenDto.builder().mensaje(e.getMessage()).build(),
                        HttpStatus.UNAUTHORIZED
                )));
    }
}
