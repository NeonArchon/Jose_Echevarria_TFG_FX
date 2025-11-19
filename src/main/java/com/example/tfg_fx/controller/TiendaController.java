package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class TiendaController {

    @FXML
    private VBox contenedorSecciones;

    private final DAO_Porducto daoProducto = new DAO_Porducto();
    private final DAO_Usuario daoUsuario = new DAO_Usuario();

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        cargarSeccionDesdeBD("Productos");
    }

    // =========================================================
    // CARGA DE SECCIÓN (PRODUCTOS)
    // =========================================================
    private void cargarSeccionDesdeBD(String titulo) {

        List<Producto> productos = daoProducto.obtenerTodosLosProductos();

        if (productos == null || productos.isEmpty()) {
            Label vacio = new Label("No hay productos en la tienda.");
            vacio.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            contenedorSecciones.getChildren().add(vacio);
            return;
        }

        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(15));
        seccion.setBackground(new Background(
                new BackgroundFill(Color.web("#f8c8dc"), new CornerRadii(10), Insets.EMPTY)
        ));

        Label labelTitulo = new Label(titulo.toUpperCase());
        labelTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox fila = new HBox(20);
        fila.setAlignment(Pos.CENTER);

        productos.stream()
                .limit(6)
                .forEach(p -> fila.getChildren().add(crearTarjetaProducto(p)));

        seccion.getChildren().addAll(labelTitulo, fila);

        contenedorSecciones.getChildren().add(seccion);
    }

    // =========================================================
    // TARJETA VISUAL DE PRODUCTO
    // =========================================================
    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(7);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 6, 0, 0, 2);");

        // TOP ROW = WISHLIST + CARRITO
        HBox top = new HBox();
        top.setAlignment(Pos.TOP_RIGHT);
        top.setSpacing(10);

        // --------------------------
        // CHECKBOX WISHLIST ❤️
        // --------------------------
        CheckBox checkWishlist = new CheckBox("❤");
        checkWishlist.setTooltip(new Tooltip("Añadir a Wishlist"));

        if (usuarioActual != null && usuarioActual.tieneEnWishlist(producto))
            checkWishlist.setSelected(true);

        checkWishlist.setOnAction(e -> {
            if (usuarioActual == null) {
                error("Debes iniciar sesión.");
                checkWishlist.setSelected(false);
                return;
            }

            if (checkWishlist.isSelected()) agregarAWishlist(producto);
            else eliminarDeWishlist(producto);
        });

        // --------------------------
        // CHECKBOX CARRITO 🛒
        // --------------------------
        CheckBox checkCarrito = new CheckBox("🛒");
        checkCarrito.setTooltip(new Tooltip("Añadir al Carrito"));

        if (usuarioActual != null && usuarioActual.tieneEnCarrito(producto))
            checkCarrito.setSelected(true);

        checkCarrito.setOnAction(e -> {
            if (usuarioActual == null) {
                error("Debes iniciar sesión.");
                checkCarrito.setSelected(false);
                return;
            }

            if (checkCarrito.isSelected()) agregarAlCarrito(producto);
            else eliminarDelCarrito(producto);
        });

        top.getChildren().addAll(checkWishlist, checkCarrito);

        // Imagen
        ImageView img = crearImageViewProducto(producto);

        // Título
        Label titulo = new Label(producto.getNombreproducto());
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        // Descripción
        Label desc = new Label(producto.getDescripcion());
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        desc.setWrapText(true);

        // Precio
        Label precio = new Label(String.format("%.2f €", producto.getPrecio()));
        precio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        card.getChildren().addAll(top, img, titulo, desc, precio);

        return card;
    }

    // =========================================================
    // CARGA DE IMÁGENES
    // =========================================================
    private ImageView crearImageViewProducto(Producto producto) {

        ImageView img = new ImageView();

        try {
            String path = "/images/" + producto.getImagenUrl();
            var stream = getClass().getResourceAsStream(path);

            if (stream != null) {
                img.setImage(new Image(stream));
            } else {
                throw new Exception("Imagen no encontrada");
            }
        } catch (Exception e) {
            img.setImage(new Image(
                    getClass().getResourceAsStream("/images/default.png")
            ));
        }

        img.setFitHeight(120);
        img.setFitWidth(120);
        img.setPreserveRatio(true);

        return img;
    }

    // =========================================================
    // WISHLIST
    // =========================================================
    private void agregarAWishlist(Producto p) {
        usuarioActual = daoUsuario.buscarPorId(usuarioActual.getId());
        usuarioActual.agregarAWishlist(p);
        daoUsuario.actualizar(usuarioActual);

        exito("Añadido a wishlist: " + p.getNombreproducto());
    }

    private void eliminarDeWishlist(Producto p) {
        usuarioActual = daoUsuario.buscarPorId(usuarioActual.getId());
        usuarioActual.eliminarDeWishlist(p);
        daoUsuario.actualizar(usuarioActual);

        info("Eliminado de wishlist: " + p.getNombreproducto());
    }

    // =========================================================
    // CARRITO
    // =========================================================
    private void agregarAlCarrito(Producto p) {
        usuarioActual = daoUsuario.buscarPorId(usuarioActual.getId());
        usuarioActual.agregarAlCarrito(p);
        daoUsuario.actualizar(usuarioActual);

        exito("Añadido al carrito: " + p.getNombreproducto());
    }

    private void eliminarDelCarrito(Producto p) {
        usuarioActual = daoUsuario.buscarPorId(usuarioActual.getId());
        usuarioActual.eliminarDelCarrito(p);
        daoUsuario.actualizar(usuarioActual);

        info("Eliminado del carrito: " + p.getNombreproducto());
    }

    // =========================================================
    // MANEJO DE USUARIO ACTUAL
    // =========================================================
    public void setUsuarioActual(Usuario usuario) {
        if (usuario != null)
            this.usuarioActual = daoUsuario.buscarPorId(usuario.getId());
        else
            this.usuarioActual = null;

        recargarVista();
    }

    private void recargarVista() {
        contenedorSecciones.getChildren().clear();
        cargarSeccionDesdeBD("Productos");
    }

    public Usuario getUsuarioActual() { return usuarioActual; }

    // =========================================================
    // MENSAJES
    // =========================================================
    private void error(String msg) { System.out.println("❌ " + msg); }
    private void exito(String msg) { System.out.println("✅ " + msg); }
    private void info(String msg)  { System.out.println("ℹ️ " + msg); }
}