package com.lms.modules.member;

import com.lms.HibernateUtil;
import com.lms.dto.MemberSearchCriteria;
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

    public Member saveMember(Member member) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Member savedMember = session.merge(member);
            tx.commit();
            return savedMember;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<Member> searchMember(MemberSearchCriteria criteria) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Member> cq = cb.createQuery(Member.class);
            Root<Member> root = cq.from(Member.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("membershipVirtualId"), criteria.getMembershipVirtualId()));
            cq.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(cq).getResultList();
        }
    }
}
