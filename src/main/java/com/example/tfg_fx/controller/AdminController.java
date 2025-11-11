package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Administrador;
import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class AdminController {

    // Paneles
    @FXML private Pane panelBorrar;
    @FXML private Pane panelAnadir;
    @FXML private Pane panelActualizar;
    @FXML private Pane panelBorrarProducto;

    // Campos para usuarios
    @FXML private TextField buscarUsuarioField;
    @FXML private TextField idUsuarioBorrarField;
    @FXML private ListView<Usuario> usuariosListView;

    // Campos para productos
    @FXML private TextField productoNombreField;
    @FXML private TextField productoPrecioField;
    @FXML private TextField productoStockField;
    @FXML private TextArea productoDescripcionField;
    @FXML private CheckBox productoOfertaCheckBox;
    @FXML private TextField buscarProductoField;
    @FXML private TextField idProductoActualizarField;
    @FXML private TextField nuevoStockField;
    @FXML private TextField idProductoBorrarField;
    @FXML private ListView<Producto> productosListView;
    @FXML private TextField productoImagenUrlField;

    private DAO_Administrador daoAdministrador;
    private DAO_Usuario daoUsuario;
    private DAO_Porducto daoProducto;

    private ObservableList<Usuario> usuariosList;
    private ObservableList<Producto> productosList;

    public AdminController() {
        daoAdministrador = new DAO_Administrador();
        daoUsuario = new DAO_Usuario();
        daoProducto = new DAO_Porducto();

        usuariosList = FXCollections.observableArrayList();
        productosList = FXCollections.observableArrayList();
    }

    private void initialize() {
        cargarUsuarios();
        cargarProductos();

        // Configurar cómo se muestran los usuarios en la ListView
        usuariosListView.setCellFactory(lv -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                } else {
                    setText("ID: " + usuario.getId() + " | " +
                            usuario.getNombre() + " " + usuario.getApellidos() +
                            " | " + usuario.getEmail());
                }
            }
        });

        // AQUÍ VA EL CÓDIGO PARA LA LISTVIEW DE PRODUCTOS - JUSTO DESPUÉS DE LA DE USUARIOS
        productosListView.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);
                if (empty || producto == null) {
                    setText(null);
                } else {
                    String imagenInfo = producto.getImagenUrl() != null ? " [Tiene imagen]" : " [Sin imagen]";
                    setText("ID: " + producto.getIdproducto() + " | " +
                            producto.getNombreproducto() + " | $" + producto.getPrecio() +
                            " | Stock: " + producto.getStock() + imagenInfo);
                }
            }
        });
    }

    // ========== MÉTODOS PARA MOSTRAR/OCULTAR PANELES ==========

    private void togglePanel(Pane panel) {
        panel.setVisible(!panel.isVisible());
        panel.setManaged(panel.isVisible());
    }

    @FXML private void toggleBorrar() { togglePanel(panelBorrar); }
    @FXML private void toggleAnadir() { togglePanel(panelAnadir); }
    @FXML private void toggleActualizar() { togglePanel(panelActualizar); }
    @FXML private void toggleBorrarProducto() { togglePanel(panelBorrarProducto); }

    // ========== GESTIÓN DE USUARIOS ==========

    @FXML
    private void handleBuscarUsuario() {
        String email = buscarUsuarioField.getText().trim();
        if (email.isEmpty()) {
            mostrarAlerta("Error", "Ingresa un email para buscar");
            return;
        }

        try {
            Usuario usuario = daoUsuario.buscarPorEmail(email);
            if (usuario != null) {
                usuariosList.clear();
                usuariosList.add(usuario);
                usuariosListView.setItems(usuariosList);
                mostrarAlerta("Éxito", "Usuario encontrado: " + usuario.getNombre());
            } else {
                mostrarAlerta("Error", "No se encontró usuario con email: " + email);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void handleBorrarUsuario() {
        String idText = idUsuarioBorrarField.getText().trim();
        if (idText.isEmpty()) {
            mostrarAlerta("Error", "Ingresa un ID de usuario");
            return;
        }

        try {
            Long idUsuario = Long.parseLong(idText);

            // Verificar si el usuario existe
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                mostrarAlerta("Error", "No se encontró usuario con ID: " + idUsuario);
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este usuario?");
            confirmacion.setContentText("Usuario: " + usuario.getNombre() + " " + usuario.getApellidos() +
                    "\nEmail: " + usuario.getEmail() +
                    "\nEsta acción no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                daoUsuario.eliminarUsuario(idUsuario);
                mostrarAlerta("Éxito", "Usuario eliminado correctamente");
                idUsuarioBorrarField.clear();
                cargarUsuarios();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID debe ser un número válido");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void handleMostrarTodosUsuarios() {
        cargarUsuarios();
        buscarUsuarioField.clear();
    }

    // ========== GESTIÓN DE PRODUCTOS ==========

    @FXML
    private void handleAnadirProducto() {
        if (!validarProducto()) return;

        try {
            Producto producto = new Producto();
            producto.setNombreproducto(productoNombreField.getText().trim());
            producto.setPrecio(Double.parseDouble(productoPrecioField.getText().trim()));
            producto.setStock(Integer.parseInt(productoStockField.getText().trim()));
            producto.setDescripcion(productoDescripcionField.getText().trim());
            producto.setOferta(productoOfertaCheckBox.isSelected());
            producto.setImagenUrl(productoImagenUrlField.getText().trim()); // Nueva línea

            daoProducto.anadirProducto(producto);
            mostrarAlerta("Éxito", "Producto añadido correctamente");
            limpiarFormularioProducto();
            cargarProductos();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al añadir producto: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizarStock() {
        String idText = idProductoActualizarField.getText().trim();
        String stockText = nuevoStockField.getText().trim();

        if (idText.isEmpty() || stockText.isEmpty()) {
            mostrarAlerta("Error", "Ingresa ID y nuevo stock");
            return;
        }

        try {
            Long idProducto = Long.parseLong(idText);
            int nuevoStock = Integer.parseInt(stockText);

            daoProducto.actualizarStock(idProducto, nuevoStock);
            mostrarAlerta("Éxito", "Stock actualizado correctamente");
            idProductoActualizarField.clear();
            nuevoStockField.clear();
            cargarProductos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID y stock deben ser números válidos");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar stock: " + e.getMessage());
        }
    }

    @FXML
    private void handleBorrarProducto() {
        String idText = idProductoBorrarField.getText().trim();
        if (idText.isEmpty()) {
            mostrarAlerta("Error", "Ingresa un ID de producto");
            return;
        }

        try {
            Long idProducto = Long.parseLong(idText);

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este producto?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                daoProducto.borrarProducto(idProducto);
                mostrarAlerta("Éxito", "Producto eliminado correctamente");
                idProductoBorrarField.clear();
                cargarProductos();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID debe ser un número válido");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar producto: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuscarProducto() {
        String nombre = buscarProductoField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "Ingresa un nombre para buscar");
            return;
        }

        try {
            List<Producto> productos = daoProducto.buscarPorNombre(nombre);
            if (productos != null && !productos.isEmpty()) {
                productosList.setAll(productos);
                productosListView.setItems(productosList);
                mostrarAlerta("Éxito", "Productos encontrados: " + productos.size());
            } else {
                mostrarAlerta("Error", "No se encontraron productos con nombre: " + nombre);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar productos: " + e.getMessage());
        }
    }

    @FXML
    private void handleMostrarTodosProductos() {
        cargarProductos();
        buscarProductoField.clear();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = daoUsuario.obtenerTodosLosUsuarios();
            if (usuarios != null && !usuarios.isEmpty()) {
                usuariosList.setAll(usuarios);
                usuariosListView.setItems(usuariosList);
                System.out.println("Usuarios cargados: " + usuarios.size());
            } else {
                usuariosList.clear();
                usuariosListView.setItems(usuariosList);
                System.out.println("No hay usuarios registrados");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        try {
            List<Producto> productos = daoProducto.obtenerTodosLosProductos();
            if (productos != null) {
                productosList.setAll(productos);
                productosListView.setItems(productosList);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage());
        }
    }

    private boolean validarProducto() {
        if (productoNombreField.getText().trim().isEmpty() ||
                productoPrecioField.getText().trim().isEmpty() ||
                productoStockField.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Nombre, precio y stock son obligatorios");
            return false;
        }

        try {
            Double.parseDouble(productoPrecioField.getText().trim());
            Integer.parseInt(productoStockField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y stock deben ser números válidos");
            return false;
        }

        return true;
    }

    private void limpiarFormularioProducto() {
        productoNombreField.clear();
        productoPrecioField.clear();
        productoStockField.clear();
        productoDescripcionField.clear();
        productoOfertaCheckBox.setSelected(false);
        productoImagenUrlField.clear();
    }

    @FXML
    private void handleSalir() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Salir");
        confirmacion.setHeaderText("¿Estás seguro de que quieres salir?");
        confirmacion.setContentText("El programa se cerrará completamente.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) panelBorrar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Planeta Maqueta - Login");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cerrar sesión");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
