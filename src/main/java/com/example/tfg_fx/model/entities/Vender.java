package com.example.tfg_fx.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.example.tfg_fx.model.entities.*;

import jakarta.persistence.*;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vender")
public class Vender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_vendedor")
    private Long idVendedor;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio_unitario")
    private double precioUnitario;

    @Column(name = "total")
    private double total;

    @Column(name = "fecha_venta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVenta;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "estado")
    private String estado;

    // Constructores
    public Vender() {}

    public Vender(Long idVendedor, Long idProducto, int cantidad,
                  double precioUnitario, Long clienteId) {
        this.idVendedor = idVendedor;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = precioUnitario * cantidad;
        this.fechaVenta = new Date();
        this.clienteId = clienteId;
        this.estado = "VENDIDO";
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdVendedor() { return idVendedor; }
    public void setIdVendedor(Long idVendedor) { this.idVendedor = idVendedor; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.total = this.precioUnitario * cantidad;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.total = precioUnitario * this.cantidad;
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Date getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Date fechaVenta) { this.fechaVenta = fechaVenta; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}