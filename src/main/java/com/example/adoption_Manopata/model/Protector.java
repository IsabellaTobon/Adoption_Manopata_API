package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protectors")
public class Protector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    private String photo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String name;

    @NotBlank(message = "El nombre de usuario es obligatorio ")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 13, message = "El teléfono no debe exceder los 13 caracteres")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "La provincia es obligatoria")
    private String province;

    @Size(max = 255)
    private String web_site;

}
