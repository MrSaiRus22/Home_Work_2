package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            logger.info("User saved: {}", user);
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error saving user", e);
            throw new RuntimeException("Database error: could not save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new RuntimeException("Database error: could not find user", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new RuntimeException("Database error: could not list users", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User merged = session.merge(user);
            tx.commit();
            logger.info("User updated: {}", merged);
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating user: {}", user.getId(), e);
            throw new RuntimeException("Database error: could not update user", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("User deleted: {}", id);
            } else {
                logger.warn("User with id {} not found for deletion", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error deleting user: {}", id, e);
            throw new RuntimeException("Database error: could not delete user", e);
        }
    }
}