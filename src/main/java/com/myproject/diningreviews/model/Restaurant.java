package com.myproject.diningreviews.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "restaurant_Details")
@Getter
@Setter
@RequiredArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Column(name = "street_name")
    private String streetName;
    private String city;
    private String state;
    @Column(name = "zip_code")
    private String zipCode;

    private String phoneNumber;
    private String website;

    private String overallScore;
    private String peanutScore;
    private String dairyScore;
    private String eggScore;
}

