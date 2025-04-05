package edu.pe.vallegrande.Security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private String uid;
    private String email;
    private String rol;
    private LocalDateTime fechaAcceso;
    private String mensaje;
}