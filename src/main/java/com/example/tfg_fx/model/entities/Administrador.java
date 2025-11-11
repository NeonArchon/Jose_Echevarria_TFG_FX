package com.example.tfg_fx.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "administrador")
public class Administrador {

    //atributios

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idadmin;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    //constructores
    public Administrador() {
    }

    public Administrador(Long idadmin, String email, String contrasena) {
        this.idadmin = idadmin;
        this.email = email;
        this.contrasena = contrasena;
    }

    //getter y setters

    public Long getIdadmin() {
        return idadmin;
    }

    public void setIdadmin(Long idadmin) {
        this.idadmin = idadmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
