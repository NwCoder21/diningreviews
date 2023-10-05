package com.myproject.diningreviews.repository;

import com.myproject.diningreviews.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);

    // Find restaurants by their name and zipCode
    Optional<Restaurant> findRestaurantsByNameAndZipCode(String name, String zipCode);

    // Find restaurants in a given zip code with non-null peanut scores, ordered by peanut score.
    List<Restaurant> findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(String zipcode);

    // Find restaurants in a given zip code with non-null dairy scores, ordered by dairy score.
    List<Restaurant> findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(String zipcode);

    // Find restaurants in a given zip code with non-null egg scores, ordered by egg score.
    List<Restaurant> findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(String zipcode);

}
