package com.lms.member;

import com.lms.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Service {
    private static final Service INSTANCE = new Service();

    private Service() {}

    public static Service getInstance() {
        return INSTANCE;
    }

    public void saveMember(Member member) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(member);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
