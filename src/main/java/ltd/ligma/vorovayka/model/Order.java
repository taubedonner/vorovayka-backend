package ltd.ligma.vorovayka.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ltd.ligma.vorovayka.config.statemachine.state.OrderStateEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal total;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStateEnum state = OrderStateEnum.RAW;

    @JsonIgnoreProperties(OrderProduct_.ORDER)
    @OneToMany(mappedBy = OrderProduct_.ORDER, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderProduct> products = new HashSet<>();

    @Schema(description = "Date after which order cannot be purchased")
    private LocalDateTime reserveExpiresIn;

    @CreatedDate
    private LocalDateTime dateCreate;

    @LastModifiedDate
    private LocalDateTime dateUpdate;
}
