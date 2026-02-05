package sun.asterisk.booking_tour.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import sun.asterisk.booking_tour.entity.User;
import sun.asterisk.booking_tour.enums.UserStatus;

public class UserSpecification implements Specification<User> {

    private final String search;
    private final UserStatus status;

    public UserSpecification(String search, UserStatus status) {
        this.search = search;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(
            jakarta.persistence.criteria.Root<User> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            Predicate firstNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")), searchPattern);
            Predicate lastNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastName")), searchPattern);
            Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), searchPattern);
            
            predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate, emailPredicate));
        }

        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
