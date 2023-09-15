package com.game.repository;

import com.game.entity.Player;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.Properties;
import java.util.List;
import java.util.Optional;


@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Configuration configuration = new Configuration();

        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "1111");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    @Transactional
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM player";
            NativeQuery<Player> query = session.createNativeQuery(sql, Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    @Override
    @Transactional
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(p) FROM Player p";
            return Math.toIntExact((Long) session.createQuery(hql).uniqueResult());
        }
    }

    @Override
    @Transactional
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }
    }
    @Override
    @Transactional
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.update(player);
            return player;
        }
    }
    @Override
    @Transactional
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.find(Player.class, id);
            return Optional.ofNullable(player);
        }
    }

    @Override
    @Transactional
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.delete(player);
        }
    }

    @PreDestroy
    public void beforeStop() {
            sessionFactory.close();
        }
    }
