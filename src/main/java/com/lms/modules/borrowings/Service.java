package com.lms.modules.borrowings;

import com.lms.HibernateUtil;
import com.lms.dto.BorrowingSearchCriteria;
import com.lms.modules.books.Book;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

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

    public List<Borrowing> searchBorrowing(BorrowingSearchCriteria bsc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Borrowing> cq = cb.createQuery(Borrowing.class);
            Root<Borrowing> root = cq.from(Borrowing.class);

            root.fetch("book", JoinType.LEFT);
            root.fetch("member", JoinType.LEFT);
            cq.select(root).distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            if (bsc.getBook() != null && bsc.getBook().getIsbn() != null) {
                Join<Borrowing, Book> bookJoin = root.join("book");
                predicates.add(
                        cb.equal(
                                bookJoin.get("isbn"),
                                bsc.getBook().getIsbn()
                        )
                );
            }
            cq.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
