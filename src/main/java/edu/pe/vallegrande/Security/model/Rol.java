package edu.pe.vallegrande.Security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class Rol {
    @Id
    private Long id;

    @Column("uid")
    private String uid;

    @Column("nombre_rol")
    private String nombreRol;
}