package ru.javaops.topjava2.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Vote extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @Column(name = "voting_date", nullable = false, updatable = false)
    private LocalDate votingDate;

    @JoinColumn(name = "restaurant_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    private Restaurant restaurant;

    public Vote(Integer id, Integer userId, LocalDate votingDate, Restaurant restaurant) {
        super(id);
        this.userId = userId;
        this.votingDate = votingDate;
        this.restaurant = restaurant;
    }
}
