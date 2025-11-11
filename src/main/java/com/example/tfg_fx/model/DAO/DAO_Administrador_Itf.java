package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Administrador;

public interface DAO_Administrador_Itf {

    void guardarAdministrador(Administrador admin);

    Administrador buscarPorEmail(String email);

    void borrarUsuario(Long idUsuario);
}
