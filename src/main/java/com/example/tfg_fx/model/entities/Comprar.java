package com.example.tfg_fx.model.entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comprar")
public class Comprar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_producto")
    private Long idProducto;

    private int cantidad;

    // Constructor vacío
    public Comprar() {}

    // Constructor con parámetros
    public Comprar(Long id, Long idCliente, Long idProducto, int cantidad) {
        this.id = id;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    // MÉTODO PARA PROCESAR COMPRA DESDE EL CARRITO
    public static List<Comprar> procesarCarrito(Usuario usuario) {
        List<Comprar> compras = new ArrayList<>();

        for (Producto producto : usuario.getCarrito()) {
            Comprar compra = new Comprar();
            compra.setIdCliente(usuario.getId());
            compra.setIdProducto(producto.getIdproducto());
            compra.setCantidad(1); // O la lógica de cantidad que necesites
            compras.add(compra);
        }

        // Vaciar carrito después de procesar
        usuario.vaciarCarrito();

        return compras;
    }

}
