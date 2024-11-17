package ltd.ligma.vorovayka.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import ltd.ligma.vorovayka.model.Order;
import ltd.ligma.vorovayka.model.Order_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
public class OrderFilter implements Specification<Order> {
    private OrderStateEnum state;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(type="string" , format = "date-time")
    private LocalDateTime createdBefore;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(type="string" , format = "date-time")
    private LocalDateTime createdAfter;

    @Override
    public Predicate toPredicate(@NonNull Root<Order> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();

        if (state != null) {
            predicates.add(cb.equal(root.get(Order_.state), state));
        }

        if (createdAfter != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.createdAt), createdAfter));
        }

        if (createdBefore != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(Order_.createdAt), createdBefore));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
