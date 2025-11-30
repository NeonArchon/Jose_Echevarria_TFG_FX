package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Producto;

import java.util.List;

public interface DAO_Producto_Itf {

    void anadirProducto(Producto producto);

    void actualizarProducto(Producto producto);

    void borrarProducto(Long idProducto);

    Producto buscarPorId(Long idProducto);

    List<Producto> buscarPorNombre(String nombre);

    List<Producto> obtenerTodosLosProductos();

    List<Producto> obtenerProductosEnOferta();

    void actualizarStock(Long idProducto, int nuevoStock);

    List<Producto> buscarPorRangoPrecios(double precioMin, double precioMax);

    List<Producto> buscarPorTipo(String tipo);

    List<Producto> obtenerWishlist();

    List<Producto> obtenerCarrito();

    void actualizarEnWishlist(Long idProducto, boolean enWishlist);

    void actualizarEnCarrito(Long idProducto, boolean enCarrito);



}
