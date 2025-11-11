package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.*;
import java.util.List;

public class DAO_Porducto implements DAO_Producto_Itf{

    @Override
    public void anadirProducto(Producto producto) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(producto);
            transaction.commit();
            System.out.println("Producto añadido correctamente: " + producto.getNombreproducto());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

    }

    @Override
    public void actualizarProducto(Producto producto) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(producto);
            transaction.commit();
            System.out.println("Producto actualizado correctamente: " + producto.getNombreproducto());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

    }

    @Override
    public void borrarProducto(Long idProducto) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Producto producto = session.get(Producto.class, idProducto);
            if (producto != null) {
                session.remove(producto);
                System.out.println("Producto eliminado: ID " + idProducto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

    }

    @Override
    public Producto buscarPorId(Long idProducto) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Producto.class, idProducto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Producto WHERE nombre LIKE :nombre", Producto.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Producto> obtenerTodosLosProductos() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Producto", Producto.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Producto> obtenerProductosEnOferta() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Producto WHERE oferta = true", Producto.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void actualizarStock(Long idProducto, int nuevoStock) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Producto producto = session.get(Producto.class, idProducto);
            if (producto != null) {
                producto.setStock(nuevoStock);
                session.merge(producto);
                System.out.println("Stock actualizado para producto ID " + idProducto + ": " + nuevoStock);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

    }

    @Override
    public List<Producto> buscarPorRangoPrecios(double precioMin, double precioMax) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Producto WHERE precio BETWEEN :min AND :max", Producto.class)
                    .setParameter("min", precioMin)
                    .setParameter("max", precioMax)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
