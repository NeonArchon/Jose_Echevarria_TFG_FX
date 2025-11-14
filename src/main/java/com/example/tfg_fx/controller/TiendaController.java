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

    // Usuario actual (se establecerá desde el login)
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        cargarSeccionDesdeBD("Minis");
    }

    /**
     * Carga una sección con productos obtenidos desde la base de datos.
     */
    private void cargarSeccionDesdeBD(String titulo) {
        // Obtener productos desde la base de datos
        List<Producto> productos = daoProducto.obtenerTodosLosProductos();

        if (productos == null || productos.isEmpty()) {
            Label vacio = new Label("No hay productos disponibles en la base de datos.");
            vacio.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
            contenedorSecciones.getChildren().add(vacio);
            return;
        }

        // VBox principal de la sección
        VBox seccion = new VBox(10);
        seccion.setPadding(new Insets(15));
        seccion.setBackground(new Background(
                new BackgroundFill(Color.web("#f8c8dc"), new CornerRadii(10), Insets.EMPTY)
        ));

        // Título
        Label labelTitulo = new Label(titulo.toUpperCase());
        labelTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Contenedor horizontal para los productos
        HBox filaProductos = new HBox(20);
        filaProductos.setAlignment(Pos.CENTER);

        // Limitar la fila a máximo 4 productos para esta primera línea
        productos.stream()
                .limit(4)
                .forEach(p -> filaProductos.getChildren().add(crearTarjetaProducto(p)));

        seccion.getChildren().addAll(labelTitulo, filaProductos);
        contenedorSecciones.getChildren().add(seccion);
    }

    /**
     * Crea la tarjeta visual de un producto con checkmark para wishlist.
     */
    private VBox crearTarjetaProducto(Producto producto) {
        VBox tarjeta = new VBox(5);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(10));
        tarjeta.setPrefWidth(180);
        tarjeta.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        tarjeta.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        // Contenedor superior para el checkmark (alineado a la derecha)
        HBox topContainer = new HBox();
        topContainer.setAlignment(Pos.TOP_RIGHT);
        topContainer.setPrefWidth(160);

        // Checkbox para wishlist
        CheckBox wishlistCheckbox = new CheckBox();
        wishlistCheckbox.setStyle("-fx-text-fill: #2c3e50;");
        wishlistCheckbox.setTooltip(new javafx.scene.control.Tooltip("Agregar a wishlist"));

        // Verificar si el producto ya está en la wishlist del usuario
        if (usuarioActual != null && usuarioActual.tieneEnWishlist(producto)) {
            wishlistCheckbox.setSelected(true);
        }

        // Acción del checkbox
        wishlistCheckbox.setOnAction(event -> {
            if (usuarioActual == null) {
                mostrarMensajeError("Debes iniciar sesión para usar la wishlist");
                wishlistCheckbox.setSelected(false);
                return;
            }

            if (wishlistCheckbox.isSelected()) {
                agregarAWishlist(producto, wishlistCheckbox);
            } else {
                eliminarDeWishlist(producto, wishlistCheckbox);
            }
        });

        topContainer.getChildren().add(wishlistCheckbox);

        // Imagen del producto
        ImageView imgView = crearImageViewProducto(producto);

        // Nombre
        Label nombre = new Label(producto.getNombreproducto());
        nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nombre.setWrapText(true);

        // Descripción
        Label descripcion = new Label(producto.getDescripcion() != null ? producto.getDescripcion() : "");
        descripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        descripcion.setWrapText(true);

        // Precio
        Label precio = new Label(String.format("%.2f €", producto.getPrecio()));
        precio.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        // Añadir elementos
        tarjeta.getChildren().addAll(topContainer, imgView, nombre, descripcion, precio);
        return tarjeta;
    }

    /**
     * Crea el ImageView para un producto con manejo de errores.
     */
    private ImageView crearImageViewProducto(Producto producto) {
        ImageView imgView = new ImageView();
        try {
            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                String imagePath = "/images/" + producto.getImagenUrl();
                java.io.InputStream inputStream = getClass().getResourceAsStream(imagePath);
                if (inputStream != null) {
                    imgView.setImage(new Image(inputStream));
                } else {
                    throw new Exception("Imagen no encontrada: " + imagePath);
                }
            } else {
                // Imagen por defecto si no hay ruta
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar la imagen para: " + producto.getNombreproducto() + " - " + e.getMessage());
            try {
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            } catch (Exception ex) {
                System.out.println("❌ No se pudo cargar la imagen por defecto");
            }
        }

        imgView.setFitWidth(120);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);
        return imgView;
    }

    /**
     * Agrega un producto a la wishlist del usuario.
     */
    private void agregarAWishlist(Producto producto, CheckBox checkBox) {
        try {
            // Recargar usuario actual desde BD para tener la versión más reciente
            Usuario usuarioRefreshed = daoUsuario.buscarPorId(usuarioActual.getId());
            if (usuarioRefreshed == null) {
                mostrarMensajeError("Error: usuario no encontrado");
                checkBox.setSelected(false);
                return;
            }

            usuarioRefreshed.agregarAWishlist(producto);
            daoUsuario.actualizar(usuarioRefreshed);

            // Actualizar referencia del usuario actual
            this.usuarioActual = usuarioRefreshed;

            System.out.println("✅ Producto agregado a wishlist: " + producto.getNombreproducto());
            mostrarMensajeExito("Agregado a wishlist: " + producto.getNombreproducto());

        } catch (Exception e) {
            System.out.println("❌ Error al agregar a wishlist: " + e.getMessage());
            mostrarMensajeError("Error al agregar a wishlist");
            checkBox.setSelected(false);
            e.printStackTrace();
        }
    }

    /**
     * Elimina un producto de la wishlist del usuario.
     */
    private void eliminarDeWishlist(Producto producto, CheckBox checkBox) {
        try {
            // Recargar usuario actual desde BD para tener la versión más reciente
            Usuario usuarioRefreshed = daoUsuario.buscarPorId(usuarioActual.getId());
            if (usuarioRefreshed == null) {
                mostrarMensajeError("Error: usuario no encontrado");
                checkBox.setSelected(true);
                return;
            }

            usuarioRefreshed.eliminarDeWishlist(producto);
            daoUsuario.actualizar(usuarioRefreshed);

            // Actualizar referencia del usuario actual
            this.usuarioActual = usuarioRefreshed;

            System.out.println("🗑️ Producto eliminado de wishlist: " + producto.getNombreproducto());
            mostrarMensajeInfo("Eliminado de wishlist: " + producto.getNombreproducto());

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar de wishlist: " + e.getMessage());
            mostrarMensajeError("Error al eliminar de wishlist");
            checkBox.setSelected(true);
            e.printStackTrace();
        }
    }

    /**
     * Método para establecer el usuario actual desde el sistema de login.
     */
    public void setUsuarioActual(Usuario usuario) {
        if (usuario != null) {
            // Cargar usuario completo desde BD para tener las relaciones
            this.usuarioActual = daoUsuario.buscarPorId(usuario.getId());
            System.out.println("👤 Usuario establecido en Tienda: " + this.usuarioActual.getNombreusuario());

            // Recargar la vista para reflejar los cambios en wishlist
            recargarVista();
        } else {
            this.usuarioActual = null;
            System.out.println("👤 Usuario desconectado de Tienda");
            recargarVista();
        }
    }

    /**
     * Recarga la vista completa.
     */
    private void recargarVista() {
        contenedorSecciones.getChildren().clear();
        cargarSeccionDesdeBD("Minis");
    }

    /**
     * Métodos auxiliares para mostrar mensajes al usuario.
     */
    private void mostrarMensajeError(String mensaje) {
        // Puedes implementar notificaciones Toast o Alert aquí
        System.out.println("❌ " + mensaje);
    }

    private void mostrarMensajeExito(String mensaje) {
        System.out.println("✅ " + mensaje);
    }

    private void mostrarMensajeInfo(String mensaje) {
        System.out.println("ℹ️ " + mensaje);
    }

    /**
     * Método para obtener el usuario actual (útil para otros controladores)
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
