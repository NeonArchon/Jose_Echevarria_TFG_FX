package com.example.tfg_fx.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    // atributos existentes...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "nombreusuario", nullable = false)
    private String nombreusuario;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "fechanacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "sexo", nullable = false)
    private String sexo;

    // NUEVOS ATRIBUTOS PARA WISHLIST Y CARRITO
    @ManyToMany
    @JoinTable(
            name = "usuario_wishlist",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> wishlist = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "usuario_carrito",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> carrito = new ArrayList<>();

    // constructores
    public Usuario() {}

    public Usuario(Long id, String nombre, String apellidos, String nombreusuario, String email, String contrasena, LocalDate fechaNacimiento, String sexo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nombreusuario = nombreusuario;
        this.email = email;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    // getters y setters existentes...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getNombreusuario() { return nombreusuario; }
    public void setNombreusuario(String nombreusuario) { this.nombreusuario = nombreusuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public List<Producto> getWishlist() { return wishlist; }
    public void setWishlist(List<Producto> wishlist) { this.wishlist = wishlist; }

    public List<Producto> getCarrito() { return carrito; }
    public void setCarrito(List<Producto> carrito) { this.carrito = carrito; }

    // MÉTODOS PARA GESTIONAR WISHLIST Y CARRITO
    public void agregarAWishlist(Producto producto) {
        if (!this.wishlist.contains(producto)) {
            this.wishlist.add(producto);
        }
    }

    public void eliminarDeWishlist(Producto producto) {
        this.wishlist.remove(producto);
    }

    public void agregarAlCarrito(Producto producto) {
        if (!this.carrito.contains(producto)) {
            this.carrito.add(producto);
        }
    }

    public void eliminarDelCarrito(Producto producto) {
        this.carrito.remove(producto);
    }

    public void vaciarCarrito() {
        this.carrito.clear();
    }

    public boolean tieneEnWishlist(Producto producto) {
        return this.wishlist.contains(producto);
    }

    public boolean tieneEnCarrito(Producto producto) {
        return this.carrito.contains(producto);
    }
}