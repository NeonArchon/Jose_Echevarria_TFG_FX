package com.example.tfg_fx.controller;


import com.example.tfg_fx.model.DAO.DAO_Producto_Itf;
import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.layout.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.util.List;

public class TiendaController {
    @FXML
    private VBox contenedorSecciones;   // coincide con tu FXML

    private final DAO_Porducto daoProducto = new DAO_Porducto();
    private final DAO_Usuario daoUsuario = new DAO_Usuario();

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        // inicializa productos en BD si es la primera ejecución
        inicializarProductos();

        // carga la vista con productos
        cargarSeccionDesdeBD("Productos");
    }

    // =========================================================
    // INICIALIZAR LOS PRODUCTOS (solo primera vez)
    // =========================================================
    private void inicializarProductos() {
        if (!daoProducto.obtenerTodosLosProductos().isEmpty()) {
            return;  // Ya existen productos → no volver a generarlos
        }

        daoProducto.anadirProducto(new Producto(null, "Trench Crusade: Peregrinos de trinchera", 20,
                "Caja para iniciar tu coleccion de los peregrinos de trinchera de Trench Crusade",
                false, 45.0, "Trench_Pilgrims.jpg", "minis", false, false));

        daoProducto.anadirProducto(new Producto(null, "Warhammer 4000: Lion El'Jhonson", 8,
                "El primarca de los Angeles Oscuros se une a una galaxia en perpetua guerra",
                false, 60.0, "Lion_Primarca_Angeles_Oscuros.jpg", "minis", false, false));

        daoProducto.anadirProducto(new Producto(null, "Warhammer AoS: Punta de Lanza Skaven", 10,
                "Caja con 25 miniaturas para empezar tu coleccion de Skaven",
                false, 115.0, "Skaven-Battleforce.jpg", "minis", false, false));
    }

    // =========================================================
    // CARGA DE SECCIÓN (PRODUCTOS)
    // =========================================================
    private void cargarSeccionDesdeBD(String titulo) {

        contenedorSecciones.getChildren().clear();

        List<Producto> productos = daoProducto.obtenerTodosLosProductos();

        if (productos == null || productos.isEmpty()) {
            Label vacio = new Label("No hay productos en la tienda.");
            vacio.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            contenedorSecciones.getChildren().add(vacio);
            return;
        }

        // Contenedor general de la sección
        VBox seccion = new VBox(20);
        seccion.setPadding(new Insets(15));
        seccion.setBackground(new Background(
                new BackgroundFill(javafx.scene.paint.Color.web("#f8c8dc"), new CornerRadii(10), Insets.EMPTY)
        ));

        Label labelTitulo = new Label(titulo.toUpperCase());
        labelTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        seccion.getChildren().add(labelTitulo);

        // -------- Crear filas de 3 productos --------
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

        // -------- Agregar hueco vacío si falta para completar 3 productos --------
        int resto = contador % 3;
        if (resto != 0) {
            int espaciosVacios = 3 - resto;
            for (int i = 0; i < espaciosVacios; i++) {
                fila.getChildren().add(crearTarjetaVacia());
            }
        }

        contenedorSecciones.getChildren().add(seccion);
    }

    private VBox crearTarjetaVacia() {
        VBox empty = new VBox();
        empty.setPrefSize(200, 240);
        empty.setBackground(new Background(
                new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, new CornerRadii(8), Insets.EMPTY)
        ));
        return empty;
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
                new BackgroundFill(javafx.scene.paint.Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 6, 0, 0, 2);");

        // TOP ROW = WISHLIST + CARRITO
        HBox top = new HBox();
        top.setAlignment(Pos.TOP_RIGHT);
        top.setSpacing(10);

        // CHECKBOX WISHLIST ❤️
        CheckBox checkWishlist = new CheckBox("❤");
        checkWishlist.setTooltip(new Tooltip("Añadir a Wishlist"));

        checkWishlist.setSelected(producto.isEnwishlist());

        checkWishlist.setOnAction(e -> {
            if (usuarioActual == null) {
                error("Debes iniciar sesión.");
                checkWishlist.setSelected(!checkWishlist.isSelected()); // revertir visualmente
                return;
            }
            boolean nuevo = checkWishlist.isSelected();
            daoProducto.actualizarEnWishlist(producto.getIdproducto(), nuevo);
            exito((nuevo ? "Añadido a wishlist: " : "Eliminado de wishlist: ") + producto.getNombreproducto());
            // recargar para reflejar cambios
            cargarSeccionDesdeBD("Productos");
        });

        // CHECKBOX CARRITO 🛒
        CheckBox checkCarrito = new CheckBox("🛒");
        checkCarrito.setTooltip(new Tooltip("Añadir al Carrito"));

        checkCarrito.setSelected(producto.isEncarrito());

        checkCarrito.setOnAction(e -> {
            if (usuarioActual == null) {
                error("Debes iniciar sesión.");
                checkCarrito.setSelected(!checkCarrito.isSelected());
                return;
            }
            boolean nuevo = checkCarrito.isSelected();
            daoProducto.actualizarEnCarrito(producto.getIdproducto(), nuevo);
            exito((nuevo ? "Añadido al carrito: " : "Eliminado del carrito: ") + producto.getNombreproducto());
            cargarSeccionDesdeBD("Productos");
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
        desc.setMaxWidth(180);

        // Precio
        Label precio = new Label(String.format("%.2f €", producto.getPrecio()));
        precio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Botón ver detalle
        Button ver = new Button("Ver");
        ver.setOnAction(e -> abrirDetallesProducto(producto));

        card.getChildren().addAll(top, img, titulo, desc, precio, ver);

        return card;
    }

    // =========================================================
    // CARGA DE IMÁGENES
    // =========================================================
    private ImageView crearImageViewProducto(Producto producto) {

        ImageView img = new ImageView();

        try {
            String path = "/images/" + producto.getImagenUrl();
            InputStream stream = getClass().getResourceAsStream(path);

            if (stream != null) {
                img.setImage(new Image(stream));
            } else {
                // si no existe imagen específica, carga default (asegúrate que existe en resources)
                InputStream def = getClass().getResourceAsStream("/images/default.png");
                if (def != null) img.setImage(new Image(def));
            }
        } catch (Exception e) {
            InputStream def = getClass().getResourceAsStream("/images/default.png");
            if (def != null) img.setImage(new Image(def));
        }

        img.setFitHeight(120);
        img.setFitWidth(120);
        img.setPreserveRatio(true);

        return img;
    }

    // =========================================================
    // DETALLES DE PRODUCTO (abre una ventana separada)
    // =========================================================
    private void abrirDetallesProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tfg_fx/detalle-producto.fxml"));
            Parent root = loader.load();

            // si no quieres la clase DetallesProductoController, puedes comentar estas 2 líneas
            Object controller = loader.getController();
            if (controller instanceof DetallesProductoController) {
                ((DetallesProductoController) controller).setProducto(producto);
            }

            Stage stage = new Stage();
            stage.setTitle("Detalles - " + producto.getNombreproducto());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Error al abrir detalles del producto.");
        }
    }

    // =========================================================
    // MANEJO DE USUARIO ACTUAL (se puede llamar desde otro controlador)
    // =========================================================
    public void setUsuarioActual(Usuario usuario) {
        if (usuario != null)
            this.usuarioActual = daoUsuario.buscarPorId(usuario.getId());
        else
            this.usuarioActual = null;

        // recarga la vista para que los checkboxes se actualicen según usuario
        cargarSeccionDesdeBD("Productos");
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // =========================================================
    // MENSAJES
    // =========================================================
    private void error(String msg) {
        System.out.println("❌ " + msg);
    }

    private void exito(String msg) {
        System.out.println("✅ " + msg);
    }

    private void info(String msg) {
        System.out.println("ℹ️ " + msg);
    }
}