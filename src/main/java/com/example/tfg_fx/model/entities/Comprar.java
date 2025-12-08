package com.example.tfg_fx.model.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comprar")
public class Comprar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @Column(name = "subtotal", nullable = false)
    private double subtotal;

    @Column(name = "fecha_compra", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompra;

    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago; // "TARJETA", "BIZUM", "TRANSFERENCIA"

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // "PENDIENTE", "COMPLETADO", "CANCELADO", "ENVIADO", "ENTREGADO"

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Column(name = "numero_referencia")
    private String numeroReferencia; // Para transferencias o referencias de pago

    // Constructor vacío
    public Comprar() {
        this.fechaCompra = new Date();
        this.estado = "PENDIENTE";
    }

    // Constructor con parámetros básicos
    public Comprar(Long idCliente, Long idProducto, int cantidad,
                   double precioUnitario, String metodoPago) {
        this();
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario * cantidad;
        this.metodoPago = metodoPago;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precioUnitario * cantidad;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario * this.cantidad;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public Date getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(Date fechaCompra) { this.fechaCompra = fechaCompra; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }

    // MÉTODO PARA PROCESAR COMPRA DESDE EL CARRITO
    public static List<Comprar> procesarCarrito(Usuario usuario, String metodoPago,
                                                String direccionEnvio, String telefonoContacto) {
        List<Comprar> compras = new ArrayList<>();
        Date fechaActual = new Date();

        for (Producto producto : usuario.getCarrito()) {
            Comprar compra = new Comprar();
            compra.setIdCliente(usuario.getId());
            compra.setIdProducto(producto.getIdproducto());
            compra.setCantidad(1); // Aquí podrías tener una cantidad específica por producto
            compra.setPrecioUnitario(producto.getPrecio());
            compra.setMetodoPago(metodoPago);
            compra.setDireccionEnvio(direccionEnvio);
            compra.setTelefonoContacto(telefonoContacto);
            compra.setFechaCompra(fechaActual);
            compra.setEstado("COMPLETADO");

            compras.add(compra);
        }

        return compras;
    }
}
