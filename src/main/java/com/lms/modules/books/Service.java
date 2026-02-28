package com.lms.modules.books;

import com.lms.HibernateUtil;
import com.lms.dto.BookSearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Service {
	private static final Service INSTANCE = new Service();

    private Service() {}

    public static Service getInstance() {
        return INSTANCE;
    }
    
		public Book saveABook(Book bookModel, Session session) {
			return (Book) session.merge(bookModel);
		}

		public void saveABook(List<Book> books) {
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

			// Extract ISBNs from the current batch only
			List<String> incomingIsbns = books.stream()
					.map(Book::getIsbn)
					.collect(Collectors.toList());

			Session session = sessionFactory.openSession();

			// Only query DB for ISBNs that exist in the incoming batch
			List<String> existingIsbns = session.createQuery(
							"SELECT b.isbn FROM Book b WHERE b.isbn IN :isbns", String.class)
					.setParameter("isbns", incomingIsbns)
					.list();

			session.close();

			// Filter out duplicates
			Set<String> existingSet = new HashSet<>(existingIsbns);
			List<Book> newBooks = books.stream()
					.filter(b -> !existingSet.contains(b.getIsbn()))
					.collect(Collectors.toList());

			// Bulk save
			Session saveSession = sessionFactory.openSession();
			Transaction tx = saveSession.beginTransaction();
			for (int i = 0; i < newBooks.size(); i++) {
				saveSession.persist(newBooks.get(i));
				if (i % 50 == 0) {
					saveSession.flush();
					saveSession.clear();
				}
			}
			tx.commit();
			saveSession.close();
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
			if (criteria.getId() != null) {
				predicates.add(cb.equal(root.get("id"), criteria.getId()));
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
