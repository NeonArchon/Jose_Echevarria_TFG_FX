package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.entities.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaProductoController {

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo; // Añadido para el subtítulo
    @FXML private ImageView imgProducto;
    @FXML private Label lblDescripcion;
    @FXML private Label lblOferta; // Etiqueta para oferta

    @FXML private Label lblPrecio;
    @FXML private Label lblStock; // Cambiado de lblObjetos
    @FXML private Label lblTipo; // Cambiado de lblDimensiones
    @FXML private Label lblDimensiones; // Nuevo para dimensiones reales
    @FXML private Label lblPeso; // Nuevo para peso real

    @FXML private Button btnComprar;
    @FXML private Button btnCarrito;
    @FXML private Button btnWishlist;

    private Producto producto;
    private DAO_Porducto daoProducto = new DAO_Porducto();

    @FXML
    public void initialize() {
        // Inicializar etiquetas como vacías
        lblSubtitulo.setText("WARHAMMER 40.000"); // Puedes hacerlo dinámico
        lblOferta.setVisible(false);
    }

    public void cargarProducto(Producto producto) {
        this.producto = producto;

        // Cargar datos básicos
        lblTitulo.setText(producto.getNombreproducto());
        lblDescripcion.setText(producto.getDescripcion());

        // Formatear precio con estilo
        lblPrecio.setText(String.format("%.2f €", producto.getPrecio()));

        // Mostrar stock con color según disponibilidad
        lblStock.setText("Stock disponible: " + producto.getStock() + " unidades");
        if (producto.getStock() <= 0) {
            lblStock.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
        } else if (producto.getStock() <= 5) {
            lblStock.setStyle("-fx-text-fill: #ffaa00; -fx-font-weight: bold;");
        } else {
            lblStock.setStyle("-fx-text-fill: #44ff44; -fx-font-weight: bold;");
        }

        lblTipo.setText("Tipo: " + producto.getTipo());

        // Manejar oferta
        if (producto.isOferta()) {
            lblOferta.setVisible(true);
            lblPrecio.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-font-size: 22px;");
        } else {
            lblOferta.setVisible(false);
            lblPrecio.setStyle("-fx-text-fill: #ffdf00; -fx-font-weight: bold; -fx-font-size: 20px;");
        }

        // Cargar imagen
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            try {
                // Intentar cargar como recurso interno primero
                String imagePath = producto.getImagenUrl();
                if (imagePath.startsWith("images/") || imagePath.startsWith("/images/")) {
                    Image image = new Image(getClass().getResourceAsStream("/com/example/tfg_fx/" + producto.getImagenUrl()));
                    imgProducto.setImage(image);
                } else {
                    // Intentar cargar desde URL externa
                    Image image = new Image(producto.getImagenUrl());
                    imgProducto.setImage(image);
                }
                imgProducto.setFitWidth(400);
                imgProducto.setPreserveRatio(true);
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen: " + producto.getImagenUrl());
                // Podrías cargar una imagen por defecto
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/tfg_fx/images/default-product.png"));
                    imgProducto.setImage(defaultImage);
                } catch (Exception ex) {
                    // Si no hay imagen por defecto, dejar vacío
                }
            }
        }

        // Configurar estado inicial de botones
        actualizarEstadoBotones();
        configurarBotones();
    }

    private void actualizarEstadoBotones() {
        // Actualizar texto de botones según estado actual
        if (producto.isEncarrito()) {
            btnCarrito.setText("QUITAR DEL CARRITO");
            btnCarrito.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        } else {
            btnCarrito.setText("AÑADIR AL CARRITO");
            btnCarrito.setStyle("-fx-background-color: #ffdf00; -fx-text-fill: black;");
        }

        if (producto.isEnwishlist()) {
            btnWishlist.setText("QUITAR DE WISHLIST");
            btnWishlist.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        } else {
            btnWishlist.setText("AÑADIR A WISHLIST");
            btnWishlist.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black;");
        }
    }

    private void configurarBotones() {
        // Comprar
     //   btnComprar.setOnAction(e -> comprarProducto());

        // Añadir/Quitar del carrito
        btnCarrito.setOnAction(e -> {
            boolean estado = !producto.isEncarrito();
            producto.setEncarrito(estado);
            daoProducto.actualizarEnCarrito(producto.getIdproducto(), estado);
            actualizarEstadoBotones();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Carrito");
            alert.setHeaderText(null);
            alert.setContentText(estado ? "✅ Producto añadido al carrito." : "❌ Producto eliminado del carrito.");
            alert.showAndWait();
        });

        // Wishlist
        btnWishlist.setOnAction(e -> {
            boolean estado = !producto.isEnwishlist();
            producto.setEnwishlist(estado);
            daoProducto.actualizarEnWishlist(producto.getIdproducto(), estado);
            actualizarEstadoBotones();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wishlist");
            alert.setHeaderText(null);
            alert.setContentText(estado ? "❤️ Producto añadido a Wishlist." : "💔 Producto eliminado de Wishlist.");
            alert.showAndWait();
        });
    }

}
