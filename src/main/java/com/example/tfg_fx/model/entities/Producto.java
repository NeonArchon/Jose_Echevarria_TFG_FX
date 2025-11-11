package com.example.tfg_fx.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "producto")
public class Producto {

    //atributios
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idproducto;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombreproducto;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "oferta")
    private boolean oferta;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "imagen_url")
    private String imagenUrl;


    //constructor vacio
    public Producto() {
    }

    //constructor con campos

    public Producto(Long idproducto, String nombreproducto, int stock, String descripcion, boolean oferta, double precio, String imagenUrl) {
        this.idproducto = idproducto;
        this.nombreproducto = nombreproducto;
        this.stock = stock;
        this.descripcion = descripcion;
        this.oferta = oferta;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    //getters y setters

    public Long getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Long idproducto) {
        this.idproducto = idproducto;
    }

    public String getNombreproducto() {
        return nombreproducto;
    }

    public void setNombreproducto(String nombreproducto) {
        this.nombreproducto = nombreproducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {return imagenUrl;}

    public void setImagenUrl(String imagenUrl) {this.imagenUrl = imagenUrl;}

    @Override
    public String toString() {
        return "Producto{" +
                "idproducto=" + idproducto +
                ", nombreproducto='" + nombreproducto + '\'' +
                ", stock=" + stock +
                ", descripcion='" + descripcion + '\'' +
                ", oferta=" + oferta +
                ", precio=" + precio +
                ", imagenUrl='" + imagenUrl + '\'' +
                '}';
    }
}
