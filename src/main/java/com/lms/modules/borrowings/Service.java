package com.lms.modules.borrowings;

import com.lms.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Service {
    private static final Service INSTANCE = new Service();

    private Service() {}

    public static Service getInstance() {
        return INSTANCE;
    }

    public Borrowing saveABooking(Borrowing borrow) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(borrow);
            tx.commit();
            return borrow;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

}
