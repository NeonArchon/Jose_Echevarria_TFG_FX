package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.*;
import com.example.tfg_fx.model.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private DAO_Usuario daoUsuario;
    private DAO_Administrador daoAdministrador;

    public HelloController() {
        daoUsuario = new DAO_Usuario();
        daoAdministrador = new DAO_Administrador();
        crearAdminInicial();
    }

    private void crearAdminInicial() {
        try {
            String emailAdmin = "admin@planeta.com";
            Administrador adminExistente = daoAdministrador.buscarPorEmail(emailAdmin);

            if (adminExistente == null) {
                Administrador adminDefault = new Administrador();
                adminDefault.setEmail(emailAdmin);
                adminDefault.setContrasena("admin123");
                daoAdministrador.guardarAdministrador(adminDefault);
                System.out.println("Administrador inicial creado.");
            } else {
                System.out.println("Administrador ya existente.");
            }
        } catch (Exception e) {
            System.err.println("Error creando admin inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Debes introducir email y contraseña");
            return;
        }

        try {
            System.out.println("Intentando login con email: " + email);

            // Primero verificar si es administrador
            Administrador admin = daoAdministrador.buscarPorEmail(email);
            if (admin != null) {
                System.out.println("Admin encontrado: " + admin.getEmail());
                if (admin.getContrasena().equals(password)) {
                    showAlert("Acceso concedido", "Bienvenido Administrador.");
                    cargarVistaAdministrador(event);
                    return;
                } else {
                    showAlert("Error", "Contraseña incorrecta para administrador");
                    return;
                }
            }

            // Si no es admin, verificar si es usuario
            Usuario usuario = daoUsuario.buscarPorEmail(email);
            if (usuario != null) {
                System.out.println("Usuario encontrado: " + usuario.getEmail());
                if (usuario.getContrasena().equals(password)) {
                    showAlert("Acceso concedido", "Bienvenido a Planeta Maqueta.");
                    // PASAMOS el objeto usuario para que podamos usar usuario.getIdusuario()
                    cargarVistaUsuario(event, usuario);
                    return;
                } else {
                    showAlert("Error", "Contraseña incorrecta para usuario");
                    return;
                }
            }

            // Mostrar opción de registro solo cuando no existe el usuario
            showOpcionesUsuarioNoEncontrado(email, password, event);

        } catch (Exception e) {
            showAlert("Error", "Error en el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showOpcionesUsuarioNoEncontrado(String email, String password, ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuario no encontrado");
        alert.setHeaderText("No se encontró una cuenta con ese email");
        alert.setContentText("¿Qué deseas hacer?\nEmail: " + email);

        ButtonType btnRegistrar = new ButtonType("Registrarse");
        ButtonType btnIntentarDeNuevo = new ButtonType("Intentar de nuevo");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnRegistrar, btnIntentarDeNuevo, btnCancelar);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnRegistrar) {
                cargarVistaRegistro(event);
            } else if (response == btnIntentarDeNuevo) {
                emailField.clear();
                passwordField.clear();
                emailField.requestFocus();
            }
        });
    }

    private void cargarVistaAdministrador(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/admin-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Administrador - Planeta Maqueta");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el panel de administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método corregido: recibe el objeto Usuario y pasa su id al PerfilController.
     */
    private void cargarVistaUsuario(ActionEvent event, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/perfil-view.fxml"));
            Parent root = loader.load();

            // Obtener controlador del FXML cargado
            com.example.tfg_fx.controller.PerfilController perfilController = loader.getController();

            // Pasar el ID del usuario (llamada de instancia, no estática)
            perfilController.setUsuarioId(usuario.getId());

            // Cargar datos en la vista (los Labels ya estarán inicializados)
            perfilController.cargarDatosUsuario();

            // Mostrar la escena
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Planeta Maqueta - Perfil de Usuario");
            stage.show();

            System.out.println("✅ Vista de usuario cargada con ID: " + usuario.getId());

        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar la vista de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarVistaRegistro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/registro_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Planeta Maqueta - Registro");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar la pantalla de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistro(ActionEvent event) {
        cargarVistaRegistro(event);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
