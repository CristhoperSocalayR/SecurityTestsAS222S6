package edu.pe.vallegrande.Security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("usuarios")
public class Usuario {
    @Id
    private Long id;

    @Column("email")
    private String email;

    @Column("uid")
    private String uid;

    @Column("nombre")
    private String nombre;

    @Column("identificador")
    private String identificador;

    @Column("proveedores")
    private String proveedores;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column("fecha_acceso")
    private LocalDateTime fechaAcceso;

    @Column("activo")
    private Boolean activo;
}
