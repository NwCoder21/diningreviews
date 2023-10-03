package com.myproject.diningreviews.repository;

import com.myproject.diningreviews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Scenario: As an application experience, I want to fetch the user profile belonging to a given display name.
    Optional<User> findUserByDisplayName(String displayName);

}
