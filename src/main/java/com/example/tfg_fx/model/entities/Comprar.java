package com.example.tfg_fx.model.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comprar")
public class Comprar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relaciones ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_compra_total")
    private CompraTotal compraTotal;

    public CompraTotal getCompraTotal() {
        return compraTotal;
    }

    public void setCompraTotal(CompraTotal compraTotal) {
        this.compraTotal = compraTotal;
    }

    // --- Datos de compra ---
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    private String metodoPago;
    private String estado;

    private String direccionEnvio;
    private String telefonoContacto;

    private LocalDateTime fechaCompra = LocalDateTime.now();

    // ============================================================
    // ✔ CONSTRUCTOR QUE NECESITA PagoController
    // ============================================================
    public Comprar(Long idUsuario,
                   Long idProducto,
                   int cantidad,
                   double precioUnitario,
                   String metodoPago) {

        this.usuario = new Usuario();
        this.usuario.setId(idUsuario);

        this.producto = new Producto();
        this.producto.setIdproducto(idProducto);

        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;

        this.metodoPago = metodoPago;
    }

    // Constructor vacío obligatorio
    public Comprar() {
    }

    // =========================
    // Getters y Setters
    // =========================
    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }
}