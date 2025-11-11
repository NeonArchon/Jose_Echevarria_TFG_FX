package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class RegistroController {

    @FXML private TextField nombreField;
    @FXML private TextField apellidosField;
    @FXML private TextField nombreUsuarioField;
    @FXML private TextField emailField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ToggleGroup sexoToggleGroup;

    private DAO_Usuario daoUsuario;

    public RegistroController() {
        daoUsuario = new DAO_Usuario();
    }

    @FXML
    private void handleRegistro(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreusuario(nombreField.getText().trim());
            nuevoUsuario.setApellidos(apellidosField.getText().trim());
            nuevoUsuario.setNombre(nombreUsuarioField.getText().trim());
            nuevoUsuario.setEmail(emailField.getText().trim());
            nuevoUsuario.setContrasena(passwordField.getText());
            nuevoUsuario.setFechaNacimiento(fechaNacimientoPicker.getValue());

            // Obtener sexo seleccionado
            RadioButton selectedRadioButton = (RadioButton) sexoToggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                nuevoUsuario.setSexo(selectedRadioButton.getUserData().toString());
            }

            // Registrar usuario en la base de datos
            daoUsuario.registrar(nuevoUsuario);

            // Mostrar mensaje de éxito
            showAlert("Registro Exitoso", "Usuario registrado correctamente. Ahora puedes iniciar sesión.");

            // Volver a la pantalla de login
            volverALogin(event);

        } catch (Exception e) {
            showAlert("Error", "No se pudo registrar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        volverALogin(event);
    }

    private boolean validarCampos() {
        // Validar campos obligatorios
        if (nombreField.getText().trim().isEmpty() ||
                apellidosField.getText().trim().isEmpty() ||
                nombreUsuarioField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                fechaNacimientoPicker.getValue() == null ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() ||
                sexoToggleGroup.getSelectedToggle() == null) {

            showAlert("Error", "Todos los campos marcados con * son obligatorios");
            return false;
        }

        // Validar email
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Error", "Por favor, introduce un email válido");
            return false;
        }

        // Validar contraseñas
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Error", "Las contraseñas no coinciden");
            return false;
        }

        // Validar longitud de contraseña
        if (passwordField.getText().length() < 6) {
            showAlert("Error", "La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        // Validar edad (mayor de 16 años)
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();
        LocalDate hoy = LocalDate.now();
        int edad = Period.between(fechaNacimiento, hoy).getYears();

        if (edad < 16) {
            showAlert("Error", "Debes tener al menos 16 años para registrarte");
            return false;
        }

        // Verificar si el email ya existe
        Usuario usuarioExistente = daoUsuario.buscarPorEmail(email);
        if (usuarioExistente != null) {
            showAlert("Error", "El email ya está registrado");
            return false;
        }

        return true;
    }

    private void volverALogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Planeta Maqueta - Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar la pantalla de login");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        // Configurar fecha máxima (hoy) para el DatePicker
        fechaNacimientoPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0);
            }
        });
    }

}
