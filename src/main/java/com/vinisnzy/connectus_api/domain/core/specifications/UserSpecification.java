package com.vinisnzy.connectus_api.domain.core.specifications;

import com.vinisnzy.connectus_api.domain.core.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UserSpecification {
    private UserSpecification(){
        throw new IllegalArgumentException("Utility class");
    }

    public static Specification<User> searchByFields(String query, UUID companyId) {
        return (root, cq, cb) -> {
            String like = "%" + query.toLowerCase() + "%";
            return cb.and(
                    cb.equal(root.get("company").get("id"), companyId),
                    cb.or(
                            cb.like(cb.lower(root.get("name")), like),
                            cb.like(cb.lower(root.get("email")), like),
                            cb.like(cb.lower(root.get("phone")), like)
                    )
            );
        };
    }
}
