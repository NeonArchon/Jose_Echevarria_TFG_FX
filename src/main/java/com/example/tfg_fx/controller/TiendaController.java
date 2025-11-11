package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.entities.Producto;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    @FXML
    public void initialize() {
        cargarSeccionDesdeBD("Minis");
    }

    /**
     * Carga una sección (por ejemplo "Minis") con productos obtenidos desde la base de datos.
     */
    private void cargarSeccionDesdeBD(String titulo) {
        // Obtener productos desde la base de datos
        List<Producto> productos = daoProducto.obtenerTodosLosProductos();

        if (productos == null || productos.isEmpty()) {
            Label vacio = new Label("No hay productos disponibles en la base de datos.");
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
     * Crea la tarjeta visual de un producto.
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

        // Imagen del producto
        ImageView imgView = new ImageView();
        try {
            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/" + producto.getImagenUrl())));
            } else {
                // Imagen por defecto si no hay ruta
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar la imagen para: " + producto.getNombreproducto());
            imgView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
        }

        imgView.setFitWidth(120);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);

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
        tarjeta.getChildren().addAll(imgView, nombre, descripcion, precio);
        return tarjeta;
    }

}
