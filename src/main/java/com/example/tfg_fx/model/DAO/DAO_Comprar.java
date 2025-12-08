package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Comprar;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class DAO_Comprar implements DAO_Comprar_Itf{


    @Override
    public boolean registrarCompra(Comprar compra) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(compra);
            tx.commit();
            return true;   // ✔ coincide con la interfaz
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;  // ✔ coincide con la interfaz
        }
    }


    @Override
    public List<Comprar> procesarCompras(List<Comprar> compras) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            for (Comprar compra : compras) {
                session.persist(compra);
            }

            tx.commit();
            return compras;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.out.println("Error procesando compras del carrito: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Comprar obtenerCompraPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Comprar.class, id);
        } catch (Exception e) {
            System.out.println("Error obteniendo compra: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Comprar> obtenerComprasPorCliente(Long idCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Comprar> q = session.createQuery(
                    "FROM Comprar c WHERE c.idCliente = :idCliente ORDER BY c.fechaCompra DESC",
                    Comprar.class
            );
            q.setParameter("idCliente", idCliente);
            return q.getResultList();
        } catch (Exception e) {
            System.out.println("Error obteniendo compras por cliente: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Comprar> obtenerComprasPorProducto(Long idProducto) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Comprar> q = session.createQuery(
                    "FROM Comprar c WHERE c.idProducto = :idProducto",
                    Comprar.class
            );
            q.setParameter("idProducto", idProducto);
            return q.getResultList();
        } catch (Exception e) {
            System.out.println("Error obteniendo compras por producto: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Comprar> obtenerTodasLasCompras() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Comprar", Comprar.class).list();
        } catch (Exception e) {
            System.out.println("Error obteniendo todas las compras: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean eliminarCompra(Long id) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Comprar compra = session.get(Comprar.class, id);
            if (compra == null) return false;

            tx = session.beginTransaction();
            session.remove(compra);
            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.out.println("Error eliminando compra: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void guardarItem(Comprar item) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(item);
            tx.commit();
        }
    }

    @Override
    public List<Comprar> obtenerItemsPorCompra(Long idCompraTotal) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Comprar c WHERE c.compraTotal.id = :idCompra",
                            Comprar.class
                    )
                    .setParameter("idCompra", idCompraTotal)
                    .getResultList();
        }
    }
}

