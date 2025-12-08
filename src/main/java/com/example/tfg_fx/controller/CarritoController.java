package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CarritoController {

    @FXML private VBox contenedorProductos;
    @FXML private Label lblTotal;
    @FXML private Button btnPagar;
    @FXML private Button btnCerrar;

    private Usuario usuario;
    private List<Producto> carrito = new ArrayList<>();
    private DAO_Usuario daoUsuario = new DAO_Usuario();

    @FXML
    public void initialize() {
        btnPagar.setOnAction(e -> abrirPantallaPago());
        btnCerrar.setOnAction(e -> cerrar());
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        if (usuario != null && usuario.getCarrito() != null) {
            carrito = new ArrayList<>(usuario.getCarrito());
        }

        mostrarCarrito();
        actualizarTotal();
    }

    private void mostrarCarrito() {
        contenedorProductos.getChildren().clear();

        if (carrito.isEmpty()) {
            Label vacio = new Label("El carrito está vacío.");
            vacio.setStyle("-fx-text-fill: white;");
            contenedorProductos.getChildren().add(vacio);
            return;
        }

        for (Producto p : carrito) {
            HBox item = new HBox(15);
            item.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-background-color: #444;");

            Label nombre = new Label(p.getNombreproducto());
            nombre.setStyle("-fx-text-fill: white; -fx-pref-width: 180;");

            Label precio = new Label(String.format("%.2f €", p.getPrecio()));
            precio.setStyle("-fx-text-fill: gold;");

            Button eliminar = new Button("X");
            eliminar.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white;");
            eliminar.setOnAction(e -> eliminarProducto(p));

            item.getChildren().addAll(nombre, precio, eliminar);
            contenedorProductos.getChildren().add(item);
        }
    }

    private void eliminarProducto(Producto p) {
        carrito.remove(p);

        usuario.getCarrito().remove(p);
        daoUsuario.actualizar(usuario);

        mostrarCarrito();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = carrito.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        lblTotal.setText(String.format("Total: %.2f €", total));
    }

    private void abrirPantallaPago() {
        if (carrito.isEmpty()) {
            mostrarError("El carrito está vacío. Añade productos antes de pagar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tfg_fx/pago-view.fxml"));

            Parent root = loader.load();
            PagoController pagoController = loader.getController();

            pagoController.setUsuario(usuario);
            pagoController.setProductos(carrito);

            Stage stage = new Stage();
            stage.setTitle("Pago de Compra");
            stage.setScene(new Scene(root));
            stage.show();

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la pantalla de pago.");
        }
    }

    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
