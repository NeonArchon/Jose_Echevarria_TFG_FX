package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.entities.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaProductoController {

    @FXML private Label lblTitulo;
    @FXML private ImageView imgProducto;
    @FXML private Label lblDescripcion;

    @FXML private Label lblPrecio;
    @FXML private Label lblObjetos;
    @FXML private Label lblDimensiones;
    @FXML private Label lblPeso;

    @FXML private Button btnComprar;
    @FXML private Button btnCarrito;
    @FXML private Button btnWishlist;

    private Producto producto;
    private DAO_Porducto daoProducto = new DAO_Porducto();

    // ============================================================
    //      MÉTODO PRINCIPAL PARA CARGAR EL PRODUCTO EN LA VISTA
    // ============================================================
    public void cargarProducto(Producto producto) {

        this.producto = producto;

        lblTitulo.setText(producto.getNombreproducto());
        lblDescripcion.setText(producto.getDescripcion());

        lblPrecio.setText("Precio: " + producto.getPrecio() + " €");
        lblObjetos.setText("Stock: " + producto.getStock());
        lblDimensiones.setText("Tipo: " + producto.getTipo());
        lblPeso.setText(producto.isOferta() ? "En oferta" : "Sin oferta");

        // Imagen
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            try {
                imgProducto.setImage(new Image(producto.getImagenUrl()));
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen: " + producto.getImagenUrl());
            }
        }

        configurarBotones();
    }

    // ============================================================
    //                    CONFIGURACIÓN DE BOTONES
    // ============================================================
    private void configurarBotones() {

        // Comprar
        btnComprar.setOnAction(e -> comprarProducto());

        // Añadir al carrito
        btnCarrito.setOnAction(e -> {
            boolean estado = !producto.isEncarrito();
            producto.setEncarrito(estado);
            daoProducto.actualizarEnCarrito(producto.getIdproducto(), estado);
            mostrarMensaje("Carrito", estado ? "Producto añadido al carrito." : "Producto eliminado del carrito.");
        });

        // Wishlist
        btnWishlist.setOnAction(e -> {
            boolean estado = !producto.isEnwishlist();
            producto.setEnwishlist(estado);
            daoProducto.actualizarEnWishlist(producto.getIdproducto(), estado);
            mostrarMensaje("Wishlist", estado ? "Producto añadido a Wishlist." : "Producto eliminado de Wishlist.");
        });
    }

    // ============================================================
    //                           COMPRAR
    // ============================================================
    private void comprarProducto() {

        if (producto.getStock() <= 0) {
            mostrarMensaje("Stock insuficiente", "Este producto no tiene stock.");
            return;
        }

        // Actualizar stock
        int nuevoStock = producto.getStock() - 1;
        producto.setStock(nuevoStock);

        daoProducto.actualizarStock(producto.getIdproducto(), nuevoStock);

        lblObjetos.setText("Stock: " + nuevoStock);

        mostrarMensaje("Compra realizada", "Has comprado: " + producto.getNombreproducto());
    }

    // ============================================================
    //                        UTILIDADES
    // ============================================================
    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }


}
