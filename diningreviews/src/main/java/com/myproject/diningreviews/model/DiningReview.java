package com.myproject.diningreviews.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "dining_Reviews")
@Getter
@Setter
@RequiredArgsConstructor

public class DiningReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submittedBy;
    private Long restaurantId;
    private String review;

    private Integer peanutScore;
    private Integer dairyScore;
    private Integer eggScore;

    private ReviewStatus status;
}

