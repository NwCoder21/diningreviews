package com.myproject.diningreviews.controller;

import com.myproject.diningreviews.model.Restaurant;
import com.myproject.diningreviews.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

@RequestMapping("/restaurants")
@RestController
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;


    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;

    }

// Submit a New Restaurant Entry
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        // Validate the new restaurant entry.
        validateNewRestaurant(restaurant);

        // Save the new restaurant to the database.
        return restaurantRepository.save(restaurant);
    }

    // Fetch Restaurant Details by Unique ID
    @GetMapping("/{id}")
    public Restaurant getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);

        // Attempt to find a restaurant by its unique ID.
        if (restaurant.isPresent()) {
            // Return the restaurant if found
            return restaurant.get();
        }

        // If not found, return a NOT_FOUND response.
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    //  Fetch Restaurants by Zip Code and Allergy Scores Sorted in Descending Order
    @GetMapping("/search")
    public Iterable<Restaurant> searchRestaurants(@RequestParam String zipcode, @RequestParam String allergy) {
        // Validate the provided zip code.

        Iterable<Restaurant> restaurants = Collections.EMPTY_LIST;

        // Determine which allergy to search for and retrieve matching restaurants.
        if (allergy.equalsIgnoreCase("peanut")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipcode);
        } else if (allergy.equalsIgnoreCase("dairy")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipcode);
        } else if (allergy.equalsIgnoreCase("egg")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipcode);
        } else {
            // If the provided allergy is not recognized, return a BAD_REQUEST response.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Return the list of matching restaurants sorted by allergy score.
        return restaurants;
    }

    // Method to validate a new restaurant entry.
    private void validateNewRestaurant(Restaurant restaurant) {
        if (ObjectUtils.isEmpty(restaurant.getName())) {
            // If the restaurant name is empty, return a BAD_REQUEST response.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Validate the format of the provided zip code.


        // Check if a restaurant with the same name and zip code already exists.
        Optional<Restaurant> existingRestaurant = restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (existingRestaurant.isPresent()) {
            // If a restaurant with the same name and zip code exists, return a CONFLICT response.
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    // Method to validate the format of a zip code.

}

