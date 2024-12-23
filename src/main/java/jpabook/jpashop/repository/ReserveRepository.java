package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Reserve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReserveRepository {

    private final EntityManager em;

    public void save(Reserve reserve) {
        em.persist(reserve);
    }

    public Reserve findOne(Long id) {
        return em.find(Reserve.class, id);
    }

    public List<Reserve> findAllByString(ReserveSearch reserveSearch) {
        //language=JPAQL
        String jpql = "select o From Reserve o join o.member m";
        boolean isFirstCondition = true;

        if (reserveSearch.getReserveStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }
            else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(reserveSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }
            else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Reserve> query = em.createQuery(jpql, Reserve.class).setMaxResults(1000); //최대 1000건
        if (reserveSearch.getReserveStatus() != null) {
            query = query.setParameter("status", reserveSearch.getReserveStatus());
        }
        if (StringUtils.hasText(reserveSearch.getMemberName())) {
            query = query.setParameter("name", reserveSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Reserve> findAllByCriteria(ReserveSearch reserveSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reserve> cq = cb.createQuery(Reserve.class);
        Root<Reserve> o = cq.from(Reserve.class);
        Join<Reserve, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        if (reserveSearch.getReserveStatus() != null) {
            Predicate status = cb.equal(o.get("status"), reserveSearch.getReserveStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(reserveSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + reserveSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Reserve> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }
}