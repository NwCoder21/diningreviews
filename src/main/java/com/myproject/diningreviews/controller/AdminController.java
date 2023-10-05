package com.myproject.diningreviews.controller;

import com.myproject.diningreviews.model.AdminReviewAction;
import com.myproject.diningreviews.model.DiningReview;
import com.myproject.diningreviews.model.Restaurant;
import com.myproject.diningreviews.model.ReviewStatus;
import com.myproject.diningreviews.repository.DiningReviewRepository;
import com.myproject.diningreviews.repository.RestaurantRepository;
import com.myproject.diningreviews.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RequestMapping("/admin")
@RestController
public class AdminController {
    private final DiningReviewRepository diningReviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AdminController(DiningReviewRepository diningReviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/reviews")
    public List<DiningReview> getReviewsByStatus(@RequestParam String review_status) {
        ReviewStatus reviewStatus = ReviewStatus.PENDING;
        try {
            reviewStatus = ReviewStatus.valueOf(review_status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return diningReviewRepository.findReviewsByStatus(reviewStatus);
    }

    @PutMapping("/reviews/{reviewId}")
    public void performReviewAction(@PathVariable Long reviewId, @RequestBody AdminReviewAction adminReviewAction) {
        // Check if the referenced dining review exists
        Optional<DiningReview> optionalReview = diningReviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dining review not found.");
        }

        DiningReview review = optionalReview.get();

        // Check if the referenced restaurant exists
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found.");
        }

        if (adminReviewAction.getAccept()) {
            review.setStatus(ReviewStatus.ACCEPTED);
        } else {
            review.setStatus(ReviewStatus.REJECTED);
        }

        diningReviewRepository.save(review);
        updateRestaurantReviewScores(optionalRestaurant.get());
    }

    private void updateRestaurantReviewScores(Restaurant restaurant) {
        List<DiningReview> reviews = diningReviewRepository.findReviewsByRestaurantIdAndStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if (reviews.size() == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int peanutSum = 0;
        int peanutCount = 0;
        int dairySum = 0;
        int dairyCount = 0;
        int eggSum = 0;
        int eggCount = 0;
        for (DiningReview r : reviews) {
            if (!ObjectUtils.isEmpty(r.getPeanutScore())) {
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if (!ObjectUtils.isEmpty(r.getDairyScore())) {
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if (!ObjectUtils.isEmpty(r.getEggScore())) {
                eggSum += r.getEggScore();
                eggCount++;
            }
        }

        int totalCount = peanutCount + dairyCount + eggCount;
        int totalSum = peanutSum + dairySum + eggSum;

        float overallScore = (float) totalSum / totalCount;

        // Determine the category based on overall score
        String category;
        if (overallScore < 2.5) {
            category = "Poor";
        } else if (overallScore < 4.5) {
            category = "Good";
        } else {
            category = "Excellent";
        }

        // Set the overallScore field to the category
        restaurant.setOverallScore(category);

        if (peanutCount > 0) {
            float peanutScore = (float) peanutSum / peanutCount;
            restaurant.setPeanutScore(decimalFormat.format(peanutScore));
        }

        if (dairyCount > 0) {
            float dairyScore = (float) dairySum / dairyCount;
            restaurant.setDairyScore(decimalFormat.format(dairyScore));
        }

        if (eggCount > 0) {
            float eggScore = (float) eggSum / eggCount;
            restaurant.setEggScore(decimalFormat.format(eggScore));
        }

        restaurantRepository.save(restaurant);
    }
}
