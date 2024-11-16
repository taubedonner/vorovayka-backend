package ltd.ligma.vorovayka.filter;

import ltd.ligma.vorovayka.model.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class ProductFilter implements Specification<Product> {
    private String name;
    private String type;
    private String manufacturer;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;

    @Override
    public Predicate toPredicate(@NonNull Root<Product> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();

        if (name != null) {
            predicates.add(cb.like(cb.lower(root.get(Product_.name)), name.toLowerCase()));
        }

        if (type != null) {
            Join<Product, ProductType> j = root.join(Product_.type);
            predicates.add(cb.like(cb.lower(j.get(ProductType_.name)), type.toLowerCase()));
        }

        if (manufacturer != null) {
            Join<Product, Manufacturer> j = root.join(Product_.manufacturer);
            predicates.add(cb.like(cb.lower(j.get(Manufacturer_.name)), manufacturer.toLowerCase()));
        }

        if (priceFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(Product_.price), priceFrom));
        }

        if (priceTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(Product_.price), priceTo));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
