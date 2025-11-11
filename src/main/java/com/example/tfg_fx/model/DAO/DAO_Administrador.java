package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Administrador;
import com.example.tfg_fx.model.entities.Producto;
import com.example.tfg_fx.model.entities.Usuario;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class DAO_Administrador implements DAO_Administrador_Itf{

    //guardar administrador
    @Override
    public void guardarAdministrador(Administrador admin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(admin);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Administrador buscarPorEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Administrador WHERE email = :email", Administrador.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Eliminar cuenta de usuario
    public void borrarUsuario(Long idUsuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Usuario usuario = session.get(Usuario.class, idUsuario);
            if (usuario != null) {
                session.remove(usuario);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }


}
