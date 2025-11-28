package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Usuario;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class DAO_Usuario implements DAO_Usuario_Itf{

    @Override
    public void registrar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
            System.out.println("✅ Usuario registrado correctamente: " + usuario.getEmail());
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            System.err.println("⚠️ Error: el email ya está registrado o existe una violación de restricción única.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(usuario); // reemplaza update() para evitar DetachedObjectException
            transaction.commit();
            System.out.println("✅ Usuario actualizado correctamente: " + usuario.getEmail());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Usuario usuario = session.createQuery(
                            "FROM Usuario WHERE email = :email", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (usuario == null) {
                System.out.println("⚠️ No se encontró usuario con email: " + email);
            }
            return usuario;
        } catch (Exception e) {
            System.err.println("❌ Error al buscar usuario por email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Usuario> usuarios = session.createQuery("FROM Usuario", Usuario.class).getResultList();
            System.out.println("✅ Usuarios obtenidos: " + usuarios.size());
            return usuarios;
        } catch (Exception e) {
            System.err.println("❌ Error al obtener lista de usuarios: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Usuario usuario = session.get(Usuario.class, idUsuario);
            if (usuario != null) {
                session.remove(usuario); // usa delete() si tu Hibernate es <6.x
                System.out.println("✅ Usuario eliminado: ID " + idUsuario);
            } else {
                System.out.println("⚠️ No se encontró usuario con ID: " + idUsuario);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Usuario buscarPorId(Long idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Usuario usuario = session.get(Usuario.class, idUsuario);
            if (usuario == null) {
                System.out.println("⚠️ No se encontró usuario con ID: " + idUsuario);
            }
            return usuario;
        } catch (Exception e) {
            System.err.println("❌ Error al buscar usuario por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Usuario obtenerUsuarioLogueado() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Usuario WHERE logueado = true", Usuario.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}