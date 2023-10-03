package com.myproject.diningreviews.repository;

import com.myproject.diningreviews.model.DiningReview;
import com.myproject.diningreviews.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiningReviewRepository extends JpaRepository<DiningReview, Long> {
    // Add custom query methods here, as needed for the scenarios.

    // ***** As part of the backend process that updates a restaurant’s set of scores,
    // I want to fetch the set of all approved dining reviews belonging to this restaurant. *****
    List<DiningReview> findReviewsByRestaurantIdAndStatus(Long restaurantId, ReviewStatus reviewStatus);


    // ***** As an admin, I want to get the list of all dining reviews that are pending approval. *****
    List<DiningReview> findReviewsByStatus(ReviewStatus reviewStatus);
}
