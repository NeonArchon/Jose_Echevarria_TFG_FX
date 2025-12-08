package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.*;
import com.example.tfg_fx.model.entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.InputStream;
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
        inicializarProductos();
    }

    private void inicializarProductos() {
        if (!daoProducto.obtenerTodosLosProductos().isEmpty()) return;

        daoProducto.anadirProducto(new Producto(null,
                "Trench Crusade: Peregrinos de trinchera", 20,
                "Caja para iniciar tu coleccion de los peregrinos de trinchera de Trench Crusade",
                false, 45.0, "images/Trench_Pilgrims.jpg", "minis", false, false));

        daoProducto.anadirProducto(new Producto(null,
                "Warhammer 4000: Lion El'Jhonson", 8,
                "El primarca de los Angeles Oscuros se une a una galaxia en perpetua guerra",
                false, 60.0, "images/Lion_Primarca_Angeles_Oscuros.jpg", "minis", false, false));

        daoProducto.anadirProducto(new Producto(null,
                "Warhammer Age of Sigmar: Punta de Lanza Skaven", 10,
                "Caja con 25 miniaturas para empezar tu coleccion de Skaven",
                false, 115.0, "images/Skaven-Battleforce.jpg", "minis", false, false));
    }

    private VBox crearTarjetaVacia() {
        VBox empty = new VBox();
        empty.setPrefSize(200, 240);
        empty.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, new CornerRadii(8), Insets.EMPTY)
        ));
        return empty;
    }

    private void cargarSeccionDesdeBD(String titulo) {

        List<Producto> productos = daoProducto.obtenerTodosLosProductos();

        if (productos == null || productos.isEmpty()) {
            Label vacio = new Label("No hay productos en la tienda.");
            vacio.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            contenedorSecciones.getChildren().add(vacio);
            return;
        }

        VBox seccion = new VBox(20);
        seccion.setPadding(new Insets(15));
        seccion.setBackground(new Background(
                new BackgroundFill(Color.web("#f8c8dc"), new CornerRadii(10), Insets.EMPTY)
        ));

        Label labelTitulo = new Label(titulo.toUpperCase());
        labelTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        seccion.getChildren().add(labelTitulo);

        HBox fila = null;
        int contador = 0;

        for (Producto p : productos) {
            if (contador % 3 == 0) {
                fila = new HBox(20);
                fila.setAlignment(Pos.CENTER_LEFT);
                seccion.getChildren().add(fila);
            }

            fila.getChildren().add(crearTarjetaProducto(p));
            contador++;
        }

        int resto = contador % 3;
        if (resto != 0) {
            int espaciosVacios = 3 - resto;
            for (int i = 0; i < espaciosVacios; i++)
                fila.getChildren().add(crearTarjetaVacia());
        }

        contenedorSecciones.getChildren().add(seccion);
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(7);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 6, 0, 0, 2);");

        // -----------------------------------------------------
        //  Zona superior: Wishlist + Carrito
        // -----------------------------------------------------
        HBox top = new HBox();
        top.setAlignment(Pos.TOP_RIGHT);
        top.setSpacing(10);
        // --- WISHLIST ---
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

        // --- CARRITO ---
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
            if (checkCarrito.isSelected()) {
                agregarAlCarrito(producto);
                abrirCarrito(); // 👈 SE ABRE AUTOMÁTICAMENTE EL CARRITO
            } else {
                eliminarDelCarrito(producto);
            }
        });

        top.getChildren().addAll(checkWishlist, checkCarrito);
        // -----------------------------------------------------
        //  IMAGEN DEL PRODUCTO (corregido)
        // -----------------------------------------------------
        ImageView imagenView = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream("/com/example/tfg_fx/" + producto.getImagenUrl());
            if (is != null) {
                imagenView.setImage(new Image(is));
            }
        } catch (Exception ex) {
            System.out.println("No se pudo cargar la imagen: " + producto.getImagenUrl());
        }

        imagenView.setFitHeight(120);
        imagenView.setFitWidth(120);
        imagenView.setPreserveRatio(true);
        // -----------------------------------------------------
        //  Texto: título, descripción, precio
        // -----------------------------------------------------
        Label titulo = new Label(producto.getNombreproducto());
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label desc = new Label(producto.getDescripcion());
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        desc.setWrapText(true);

        Label precio = new Label(String.format("%.2f €", producto.getPrecio()));
        precio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        // -----------------------------------------------------
        //  Evento → Abrir detalles
        // -----------------------------------------------------
        card.setOnMouseClicked(e -> abrirDetallesProducto(producto));
        // -----------------------------------------------------
        //  Añadir al contenedor principal
        // -----------------------------------------------------
        card.getChildren().addAll(top, imagenView, titulo, desc, precio);

        return card;
    }

    private void abrirDetallesProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tfg_fx/producto-view.fxml")); // Asegúrate que este es el nombre correcto del archivo FXML

            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof PantallaProductoController) {
                ((PantallaProductoController) controller).cargarProducto(producto); // Llamar al método cargarProducto
            }

            Stage stage = new Stage();
            stage.setTitle("Detalles del Producto");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirPantallaPago() {
        if (usuarioActual == null) {
            error("Debes iniciar sesión para realizar una compra.");
            return;
        }

        if (usuarioActual.getCarrito().isEmpty()) {
            error("El carrito está vacío.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tfg_fx/pantalla-pago.fxml"));
            Parent root = loader.load();

            PagoController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = new Stage();
            stage.setTitle("Procesar Pago");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirCarrito() {
        if (usuarioActual == null) {
            error("Debes iniciar sesión.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tfg_fx/carrito-view.fxml"));
            Parent root = loader.load();

            CarritoController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = new Stage();
            stage.setTitle("Carrito");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = (usuario != null)
                ? daoUsuario.buscarPorId(usuario.getId())
                : null;

        recargarVista();
    }

    private void recargarVista() {
        contenedorSecciones.getChildren().clear();
        cargarSeccionDesdeBD("Productos");
    }

    private void error(String msg) { System.out.println("❌ " + msg); }
    private void exito(String msg) { System.out.println("✅ " + msg); }
    private void info(String msg)  { System.out.println("ℹ️ " + msg); }
}
