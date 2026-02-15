package com.lms.db;

import com.lms.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Function;

public class TxManager {
    public static <T> T execute(Function<Session, T> action) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                T result = action.apply(session);
                tx.commit();
                return result;

            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
