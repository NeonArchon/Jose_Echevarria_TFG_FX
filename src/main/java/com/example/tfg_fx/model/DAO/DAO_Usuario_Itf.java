package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Usuario;

import java.util.List;

public interface DAO_Usuario_Itf {

    void registrar(Usuario usuario);
    void actualizar(Usuario usuario);
    Usuario buscarPorEmail(String email);
    List<Usuario> obtenerTodosLosUsuarios();
    void eliminarUsuario(Long idUsuario);
    Usuario buscarPorId(Long idUsuario);
    public Usuario obtenerUsuarioLogueado();
}
