package com.lms.modules.books;

import com.lms.HibernateUtil;
import com.lms.dto.BookSearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
    
	public void saveABook(Book bookModel) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.merge(bookModel);
			tx.commit();
		} catch (Exception e) {
			if (tx != null) tx.rollback();
            throw e;
		}
	}

	public List<Book> searchBooks(BookSearchCriteria criteria) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Book> cq = cb.createQuery(Book.class);
			Root<Book> root = cq.from(Book.class);

			List<Predicate> predicates = new ArrayList<>();

			if (criteria.getIsbn() != null) {
				predicates.add(cb.equal(root.get("isbn"), criteria.getIsbn()));
			}
			if (criteria.getTitle() != null) {
				predicates.add(cb.like(cb.lower(root.get("title")), '%' + criteria.getTitle().toLowerCase() + '%'));
			}
			if (criteria.getAuthor() !=  null) {
				predicates.add(cb.like(cb.lower(root.get("author")), '%' + criteria.getAuthor().toLowerCase() + '%'));
			}
			cq.where(predicates.toArray(new Predicate[0]));
			if ("price".equalsIgnoreCase(criteria.getSortBy())) {
				cq.orderBy(
					"desc".equals(criteria.getSortOrder()) ? cb.desc(root.get("price")) : cb.asc(root.get("price"))
				);
			}
			return session.createQuery(cq).getResultList();
		}
	}
}
