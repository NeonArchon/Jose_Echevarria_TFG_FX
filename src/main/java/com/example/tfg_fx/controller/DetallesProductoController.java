package com.example.tfg_fx.controller;

import com.example.tfg_fx.model.entities.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;


public class DetallesProductoController {

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblDescripcion;

    @FXML
    private Label lblPrecio;

    @FXML
    private ImageView imgProducto;

    private Producto producto;

    public void setProducto(Producto producto) {
        this.producto = producto;
        actualizarVista();
    }

    private void actualizarVista() {
        if (producto == null) return;

        lblNombre.setText(producto.getNombreproducto());
        lblDescripcion.setText(producto.getDescripcion());
        lblPrecio.setText(String.format("%.2f €", producto.getPrecio()));

        try {
            String path = "/images/" + producto.getImagenUrl();
            InputStream is = getClass().getResourceAsStream(path);

            if (is != null) {
                imgProducto.setImage(new Image(is));
            } else {
                imgProducto.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } catch (Exception e) {
            imgProducto.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
        }
    }
}
