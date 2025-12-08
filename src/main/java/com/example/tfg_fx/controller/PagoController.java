package com.example.tfg_fx.controller;

import javafx.event.ActionEvent;
import com.example.tfg_fx.model.DAO.DAO_Comprar;
import com.example.tfg_fx.model.DAO.DAO_Porducto;
import com.example.tfg_fx.model.DAO.DAO_Usuario;
import com.example.tfg_fx.model.entities.Comprar;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class PagoController {
    @FXML private Label lblTotal;
    @FXML private Label lblTotalCalculado;
    @FXML private VBox contenedorProductos;

    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;

    @FXML private ToggleGroup grupoPago;

    @FXML private VBox panelTarjeta;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtFechaExpiracion;
    @FXML private TextField txtCVV;
    @FXML private TextField txtTitular;

    @FXML private VBox panelBizum;
    @FXML private TextField txtTelefonoBizum;

    @FXML private VBox panelTransferencia;

    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private Usuario usuario;
    private List<Producto> productosCarrito;
    private double total;

    private DAO_Usuario daoUsuario = new DAO_Usuario();
    private DAO_Porducto daoProducto = new DAO_Porducto();
    private DAO_Comprar daoComprar = new DAO_Comprar();

    @FXML
    public void initialize() {
        productosCarrito = new ArrayList<>();

        // Configurar listeners para los métodos de pago
        grupoPago.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String metodo = (String) newToggle.getUserData();
                mostrarPanelPago(metodo);
            }
        });

        // Configurar botones
        btnCancelar.setOnAction(e -> cancelarPago());
        btnConfirmar.setOnAction(e -> confirmarPago());
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            cargarCarritoUsuario();
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productosCarrito = productos;
        calcularTotal();
        mostrarProductos();
    }

    private void cargarCarritoUsuario() {
        if (usuario.getCarrito() != null) {
            productosCarrito = new ArrayList<>(usuario.getCarrito());
            calcularTotal();
            mostrarProductos();
        }
    }

    private void calcularTotal() {
        total = 0.0;
        for (Producto p : productosCarrito) {
            total += p.getPrecio();
        }

        lblTotal.setText(String.format("Total: %.2f €", total));
        lblTotalCalculado.setText(String.format("%.2f €", total));
    }

    private void mostrarProductos() {
        contenedorProductos.getChildren().clear();

        for (Producto p : productosCarrito) {
            HBox item = new HBox(10);
            item.setStyle("-fx-padding: 5; -fx-background-color: #444; -fx-background-radius: 5;");

            Label nombre = new Label(p.getNombreproducto());
            nombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200;");

            Label precio = new Label(String.format("%.2f €", p.getPrecio()));
            precio.setStyle("-fx-text-fill: #ffdf00; -fx-font-weight: bold;");

            item.getChildren().addAll(nombre, precio);
            contenedorProductos.getChildren().add(item);
        }
    }

    private void mostrarPanelPago(String metodo) {
        // Ocultar todos los paneles
        panelTarjeta.setVisible(false);
        panelBizum.setVisible(false);
        panelTransferencia.setVisible(false);

        // Mostrar el panel correspondiente
        switch (metodo) {
            case "TARJETA":
                panelTarjeta.setVisible(true);
                break;
            case "BIZUM":
                panelBizum.setVisible(true);
                break;
            case "TRANSFERENCIA":
                panelTransferencia.setVisible(true);
                break;
        }
    }

    private boolean validarDatos() {
        // Validar dirección
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarError("Dirección requerida", "Por favor, introduce una dirección de envío.");
            return false;
        }

        // Validar teléfono
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarError("Teléfono requerido", "Por favor, introduce un teléfono de contacto.");
            return false;
        }

        // Validar método de pago seleccionado
        if (grupoPago.getSelectedToggle() == null) {
            mostrarError("Método de pago", "Por favor, selecciona un método de pago.");
            return false;
        }

        String metodo = (String) grupoPago.getSelectedToggle().getUserData();

        // Validaciones específicas por método
        switch (metodo) {
            case "TARJETA":
                if (!validarTarjeta()) return false;
                break;
            case "BIZUM":
                if (!validarBizum()) return false;
                break;
            case "TRANSFERENCIA":
                // Transferencia no necesita validación adicional
                break;
        }

        // Validar stock
        for (Producto p : productosCarrito) {
            Producto productoBD = daoProducto.buscarPorId(p.getIdproducto());
            if (productoBD.getStock() <= 0) {
                mostrarError("Stock insuficiente",
                        "El producto '" + p.getNombreproducto() + "' no tiene stock disponible.");
                return false;
            }
        }

        return true;
    }

    private boolean validarTarjeta() {
        if (txtNumeroTarjeta.getText().trim().isEmpty()) {
            mostrarError("Tarjeta", "Introduce el número de tarjeta.");
            return false;
        }
        if (txtFechaExpiracion.getText().trim().isEmpty()) {
            mostrarError("Tarjeta", "Introduce la fecha de expiración.");
            return false;
        }
        if (txtCVV.getText().trim().isEmpty()) {
            mostrarError("Tarjeta", "Introduce el CVV.");
            return false;
        }
        if (txtTitular.getText().trim().isEmpty()) {
            mostrarError("Tarjeta", "Introduce el titular de la tarjeta.");
            return false;
        }
        return true;
    }

    private boolean validarBizum() {
        if (txtTelefonoBizum.getText().trim().isEmpty()) {
            mostrarError("Bizum", "Introduce el número de teléfono para Bizum.");
            return false;
        }
        return true;
    }

    private void confirmarPago() {
        if (!validarDatos()) {
            return;
        }

        try {
            String metodoPago = (String) grupoPago.getSelectedToggle().getUserData();

            // 1. Crear registros de compra
            List<Comprar> compras = new ArrayList<>();
            for (Producto p : productosCarrito) {
                Comprar compra = new Comprar(
                        usuario.getId(),
                        p.getIdproducto(),
                        1, // cantidad
                        p.getPrecio(),
                        metodoPago
                );
                compra.setDireccionEnvio(txtDireccion.getText());
                compra.setTelefonoContacto(txtTelefono.getText());
                compra.setEstado("COMPLETADO");

                // Guardar en BD
                daoComprar.registrarCompra(compra);
                compras.add(compra);

                // 2. Actualizar stock
                Producto productoBD = daoProducto.buscarPorId(p.getIdproducto());
                int nuevoStock = productoBD.getStock() - 1;
                daoProducto.actualizarStock(p.getIdproducto(), nuevoStock);
            }

            // 3. Vaciar carrito del usuario
            usuario.vaciarCarrito();
            daoUsuario.actualizar(usuario);

            // 4. Mostrar confirmación
            mostrarConfirmacion(compras);

            // 5. Cerrar ventana
            Stage stage = (Stage) btnConfirmar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error en el pago", "Ocurrió un error al procesar el pago: " + e.getMessage());
        }
    }

    private void mostrarConfirmacion(List<Comprar> compras) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("✅ COMPRA REALIZADA CON ÉXITO\n\n");
        mensaje.append("Detalles de la compra:\n");

        for (Comprar c : compras) {
            mensaje.append(String.format("- Producto ID: %d | Cantidad: %d | Total: %.2f €\n",
                    c.getId(), c.getCantidad(), c.getSubtotal()));
        }

        mensaje.append(String.format("\nTotal pagado: %.2f €", total));
        mensaje.append("\n\nSe ha enviado un correo de confirmación.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra Exitosa");
        alert.setHeaderText("¡Gracias por tu compra!");
        alert.setContentText(mensaje.toString());
        alert.showAndWait();
    }

    private void cancelarPago() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
