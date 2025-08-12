package com.example.marketplace.repository;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.model.HouseHelp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HouseHelpCustomRepositoryImpl implements HouseHelpCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HouseHelp> findByFilter(HouseHelpFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HouseHelp> query = cb.createQuery(HouseHelp.class);
        Root<HouseHelp> root = query.from(HouseHelp.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("verified")));

        if (filter.getExperience() != null) {
            predicates.add(cb.ge(root.get("experienceYears"), filter.getExperience()));
        }

        if (filter.getAvailability() != null) {
            predicates.add(cb.equal(root.get("availability"), filter.getAvailability()));
        }
        if (filter.getMinExpectedSalary() != null) {
            predicates.add(cb.ge(root.get("expectedSalary"), filter.getMinExpectedSalary()));
        }
        if (filter.getMaxExpectedSalary() != null) {
            predicates.add(cb.le(root.get("expectedSalary"), filter.getMaxExpectedSalary()));
        }
        if (filter.getLanguages() != null) {
            predicates.add(cb.like(cb.lower(root.get("languages")), "%" + filter.getLanguages().toLowerCase() + "%"));
        }
        if (filter.getNumberOfChildren() != null) {
            predicates.add(cb.ge(root.get("numberOfChildren"), filter.getNumberOfChildren()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
