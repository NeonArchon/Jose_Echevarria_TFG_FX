package com.example.tfg_fx.model.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra_total")
public class CompraTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relación bidireccional -> CompraTotal → muchos Comprar
    @OneToMany(mappedBy = "compraTotal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comprar> itemsCompra = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

    @Column(name = "fecha")
    private LocalDate fecha;

    public void addItem(Comprar item) {
        itemsCompra.add(item);
        item.setCompraTotal(this);
    }

    public void removeItem(Comprar item) {
        itemsCompra.remove(item);
        item.setCompraTotal(null);
    }

    // getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Comprar> getItemsCompra() {
        return itemsCompra;
    }

    public void setItemsCompra(List<Comprar> itemsCompra) {
        this.itemsCompra = itemsCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
