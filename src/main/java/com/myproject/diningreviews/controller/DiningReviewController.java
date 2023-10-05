package com.myproject.diningreviews.controller;

import com.myproject.diningreviews.model.DiningReview;
import com.myproject.diningreviews.model.Restaurant;
import com.myproject.diningreviews.model.ReviewStatus;
import com.myproject.diningreviews.model.User;
import com.myproject.diningreviews.repository.DiningReviewRepository;
import com.myproject.diningreviews.repository.RestaurantRepository;
import com.myproject.diningreviews.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/reviews")
@RestController
public class DiningReviewController {
    private final DiningReviewRepository diningReviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository; // Inject the RestaurantRepository

    public DiningReviewController(DiningReviewRepository diningReviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // ***** As a registered user, I want to submit a dining review. *****
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addDiningReview(@RequestBody DiningReview diningReview) {
        validateDiningReview(diningReview);

        // Check if the referenced restaurant exists
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(diningReview.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found.");
        }


        diningReview.setStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(diningReview);
    }

    // Retrieve a dining review by its database ID (reviewId) via a GET request to the /reviews/{reviewId} endpoint
    @GetMapping("/reviews/{reviewId}")
    public DiningReview getDiningReview(@PathVariable Long reviewId) {
        Optional<DiningReview> review = diningReviewRepository.findById(reviewId);
        if (review.isPresent()) {
            return review.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private void validateDiningReview(DiningReview diningReview) {
        if (ObjectUtils.isEmpty(diningReview.getSubmittedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SubmittedBy field is required.");
        }

        if (ObjectUtils.isEmpty(diningReview.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RestaurantId field is required.");
        }

        if (ObjectUtils.isEmpty(diningReview.getPeanutScore()) &&
                ObjectUtils.isEmpty(diningReview.getDairyScore()) &&
                ObjectUtils.isEmpty(diningReview.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one score field (PeanutScore, DairyScore, or EggScore) is required.");
        }

        Optional<User> optionalUser = userRepository.findUserByDisplayName(diningReview.getSubmittedBy());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
