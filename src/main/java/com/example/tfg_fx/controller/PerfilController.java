package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class PerfilController {

    // Campos editables
    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField nombreUsuarioField;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker fechaNacimientoPicker;
    @FXML
    private ComboBox<String> sexoComboBox;

    // Botones
    @FXML
    private Button guardarButton;
    @FXML
    private Button cancelarButton;
    @FXML
    private Button cambiarDatosButton;
    @FXML
    private Button irATiendaButton; // Asegúrate de que este botón existe en tu FXML

    private DAO_Usuario usuarioDAO;
    private Usuario usuarioActual;
    private Long usuarioId;
    private boolean modoEdicion = false;

    // Para guardar los valores originales al cancelar
    private Usuario usuarioOriginal;

    public PerfilController() {
        this.usuarioDAO = new DAO_Usuario();
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @FXML
    public void initialize() {
        configurarComboBox();
        configurarEstilosCampos();
        configurarBotones();
    }

    private void configurarComboBox() {
        // Configurar opciones del ComboBox
        sexoComboBox.getItems().addAll("MASCULINO", "FEMENINO", "OTRO");
    }

    private void configurarEstilosCampos() {
        // Estilo inicial para campos no editables
        String estiloNoEditable = "-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-text-fill: #666;";
        nombreField.setStyle(estiloNoEditable);
        apellidosField.setStyle(estiloNoEditable);
        nombreUsuarioField.setStyle(estiloNoEditable);
        emailField.setStyle(estiloNoEditable);
        fechaNacimientoPicker.setStyle(estiloNoEditable);
        sexoComboBox.setStyle(estiloNoEditable);
    }

    private void configurarBotones() {
        // Configurar estilo del botón de tienda
        if (irATiendaButton != null) {
            irATiendaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            irATiendaButton.setOnMouseEntered(e -> irATiendaButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold;"));
            irATiendaButton.setOnMouseExited(e -> irATiendaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"));
        }
    }

    public void cargarDatosUsuario() {
        if (usuarioId != null) {
            usuarioActual = usuarioDAO.buscarPorId(usuarioId);

            if (usuarioActual != null) {
                mostrarDatosUsuario();
                // Guardar copia para cancelar
                usuarioOriginal = clonarUsuario(usuarioActual);
                System.out.println("✅ Datos del usuario cargados: " + usuarioActual.getNombre());

                // Habilitar botón de tienda ahora que tenemos usuario
                if (irATiendaButton != null) {
                    irATiendaButton.setDisable(false);
                }
            } else {
                System.err.println("❌ No se pudo cargar el usuario con ID: " + usuarioId);
                mostrarError("No se pudieron cargar los datos del usuario");
                // Deshabilitar botón de tienda si no hay usuario
                if (irATiendaButton != null) {
                    irATiendaButton.setDisable(true);
                }
            }
        } else {
            System.err.println("❌ No se ha establecido el ID del usuario");
            mostrarError("No hay usuario identificado");
            // Deshabilitar botón de tienda si no hay usuario
            if (irATiendaButton != null) {
                irATiendaButton.setDisable(true);
            }
        }
    }

    private void mostrarDatosUsuario() {
        try {
            // Cargar datos en los campos
            nombreField.setText(usuarioActual.getNombre());
            apellidosField.setText(usuarioActual.getApellidos());
            nombreUsuarioField.setText(usuarioActual.getNombreusuario());
            emailField.setText(usuarioActual.getEmail());
            fechaNacimientoPicker.setValue(usuarioActual.getFechaNacimiento());
            sexoComboBox.setValue(usuarioActual.getSexo());

            // Deshabilitar edición inicialmente
            habilitarEdicion(false);

        } catch (Exception e) {
            System.err.println("❌ Error al mostrar datos del usuario: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar los datos");
        }
    }

    /**
     * Método para el botón "Cambiar datos" - Activa modo edición
     */
    @FXML
    private void handleCambiarDatos() {
        System.out.println("Activando modo edición...");
        habilitarEdicion(true);
        modoEdicion = true;

        // Mostrar botones guardar/cancelar
        guardarButton.setVisible(true);
        cancelarButton.setVisible(true);

        // Cambiar estilo de campos a editables
        String estiloEditable = "-fx-background-color: white; -fx-border-color: #4CAF50; -fx-text-fill: black;";
        nombreField.setStyle(estiloEditable);
        apellidosField.setStyle(estiloEditable);
        nombreUsuarioField.setStyle(estiloEditable);
        emailField.setStyle(estiloEditable);
        fechaNacimientoPicker.setStyle(estiloEditable);
        sexoComboBox.setStyle(estiloEditable);
    }

    /**
     * Método para el botón "Guardar cambios"
     */
    @FXML
    private void handleGuardarCambios() {
        if (validarDatos()) {
            guardarCambios();
        }
    }

    /**
     * MÉTODO COMPLETADO: Botón "Ir a Tienda"
     */
    @FXML
    private void handleIrATienda(ActionEvent event) throws IOException {

        // 👉 OBTENER USUARIO DEL PERFIL, NO DE SINGLETON
        Usuario usuario = usuarioActual;

        if (usuario == null) {
            System.out.println("❌ No hay usuario cargado en PerfilController.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/tienda-view.fxml"));
        Parent tiendaRoot = loader.load();

        // 👉 Pasar usuario al controlador de tienda
        TiendaController tiendaController = loader.getController();
        DAO_Usuario dao = new DAO_Usuario();
        Usuario usuarioFull = dao.obtenerUsuarioConListas(getUsuarioActual().getId());

        tiendaController.setUsuarioActual(usuarioFull);

        Scene tiendaScene = new Scene(tiendaRoot);

        Stage currentStage = (Stage) irATiendaButton.getScene().getWindow();

        Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

        currentStage.setScene(tiendaScene);
        currentStage.setX(bounds.getMinX());
        currentStage.setY(bounds.getMinY());
        currentStage.setWidth(bounds.getWidth());
        currentStage.setHeight(bounds.getHeight());
    }

    /**
     * Método para el botón "Cancelar" - Restaura valores originales
     */
    @FXML
    private void handleCancelar() {
        System.out.println("Cancelando edición...");

        // Restaurar valores originales
        if (usuarioOriginal != null) {
            usuarioActual = clonarUsuario(usuarioOriginal);
            mostrarDatosUsuario();
        }

        modoEdicion = false;
        guardarButton.setVisible(false);
        cancelarButton.setVisible(false);

        // Restaurar estilo de campos no editables
        configurarEstilosCampos();
    }

    private void habilitarEdicion(boolean habilitar) {
        nombreField.setEditable(habilitar);
        apellidosField.setEditable(habilitar);
        nombreUsuarioField.setEditable(habilitar);
        emailField.setEditable(habilitar);
        fechaNacimientoPicker.setDisable(!habilitar);
        sexoComboBox.setDisable(!habilitar);
    }

    private boolean validarDatos() {
        // Validar campos obligatorios
        if (nombreField.getText().trim().isEmpty() ||
                apellidosField.getText().trim().isEmpty() ||
                nombreUsuarioField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                fechaNacimientoPicker.getValue() == null ||
                sexoComboBox.getValue() == null) {

            mostrarAlerta("Error de validación", "Todos los campos son obligatorios");
            return false;
        }

        // Validar formato de email
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta("Error de validación", "El formato del email no es válido");
            return false;
        }

        // Validar que la fecha de nacimiento sea en el pasado
        if (fechaNacimientoPicker.getValue().isAfter(LocalDate.now())) {
            mostrarAlerta("Error de validación", "La fecha de nacimiento no puede ser futura");
            return false;
        }


        // Validar que el email no esté en uso (excepto por el usuario actual)
        String nuevoEmail = emailField.getText().trim();
        Usuario emailExistente = usuarioDAO.buscarPorEmail(nuevoEmail);
        if (emailExistente != null && !emailExistente.getId().equals(usuarioActual.getId())) {
            mostrarAlerta("Error de validación", "El email ya está registrado");
            return false;
        }

        return true;
    }

    private void guardarCambios() {
        try {
            // Actualizar objeto usuario con los nuevos datos
            usuarioActual.setNombre(nombreField.getText().trim());
            usuarioActual.setApellidos(apellidosField.getText().trim());
            usuarioActual.setNombreusuario(nombreUsuarioField.getText().trim());
            usuarioActual.setEmail(emailField.getText().trim());
            usuarioActual.setFechaNacimiento(fechaNacimientoPicker.getValue());
            usuarioActual.setSexo(sexoComboBox.getValue());

            // Guardar en base de datos
            usuarioDAO.actualizar(usuarioActual);

            // Actualizar copia original
            usuarioOriginal = clonarUsuario(usuarioActual);

            // Salir del modo edición
            habilitarEdicion(false);
            modoEdicion = false;
            guardarButton.setVisible(false);
            cancelarButton.setVisible(false);

            // Restaurar estilo de campos no editables
            configurarEstilosCampos();

            mostrarAlerta("Éxito", "Datos actualizados correctamente");
            System.out.println("✅ Datos del usuario actualizados: " + usuarioActual.getNombre());

        } catch (Exception e) {
            System.err.println("❌ Error al guardar cambios: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron guardar los cambios: " + e.getMessage());
        }
    }

    private Usuario clonarUsuario(Usuario original) {
        return new Usuario(
                original.getId(),
                original.getNombre(),
                original.getApellidos(),
                original.getNombreusuario(),
                original.getEmail(),
                original.getContrasena(),
                original.getFechaNacimiento(),
                original.getSexo()
        );
    }

    private void mostrarError(String mensaje) {
        nombreField.setText(mensaje);
        apellidosField.setText("");
        nombreUsuarioField.setText("");
        emailField.setText("");
        fechaNacimientoPicker.setValue(null);
        sexoComboBox.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Getters
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }
}