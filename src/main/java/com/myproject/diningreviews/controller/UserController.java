package com.myproject.diningreviews.controller;

import com.myproject.diningreviews.model.User;
import com.myproject.diningreviews.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {

    // ###################################################################
    private final UserRepository userRepository;
    // ###################################################################


    // ###################################################################
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // ###################################################################


    // New mapping to retrieve a user by ID
    @GetMapping("id/{id}")
    public User getUserById(@PathVariable Long id) {
        validateUserId(id);

        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User existingUser = optionalUser.get();
        // You might want to remove sensitive data like ID or other fields here.
        return existingUser;
    }


    // ***** As an unregistered user, I want to create my user profile using a display name that’s unique only to me. *****
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody User user) {
        validateUser(user);

        userRepository.save(user);
    }

    // ***** Method to fetch the user profile belonging to a given display name *****
    @GetMapping("/{displayName}")
    public User getUser(@PathVariable String displayName) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);

        if (!optionalExistingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User existingUser = optionalExistingUser.get();
        existingUser.setId(null);

        return existingUser;
    }

    // ***** As a registered user, I want to update my user profile. I cannot modify my unique display name. *****
    @PutMapping("/{displayName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserInfo(@PathVariable String displayName, @RequestBody User updatedUser) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User existingUser = optionalExistingUser.get();

        copyUserInfoFrom(updatedUser, existingUser);
        userRepository.save(existingUser);
    }

    // ***** Updating the user information of an existing user based on the information provided in an updated user object *****
    private void copyUserInfoFrom(User updatedUser, User existingUser) {
        if (ObjectUtils.isEmpty(updatedUser.getDisplayName())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!ObjectUtils.isEmpty(updatedUser.getCity())) {
            existingUser.setCity(updatedUser.getCity());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getState())) {
            existingUser.setState(updatedUser.getState());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getZipCode())) {
            existingUser.setZipCode(updatedUser.getZipCode());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getPeanutWatch())) {
            existingUser.setPeanutWatch(updatedUser.getPeanutWatch());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getDairyWatch())) {
            existingUser.setDairyWatch(updatedUser.getDairyWatch());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getEggWatch())) {
            existingUser.setEggWatch(updatedUser.getEggWatch());
        }
    }
    // ###################################################################


    // New validation method for user ID
    private void validateUserId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID");
        }
    }


    // ***** As part of the backend process that validates a user-submitted dining review,
    // I want to verify that the user exists, based on the user display name associated with the dining review. *****
    private void validateUser(User user) {
        validateDisplayName(user.getDisplayName());

        Optional<User> existingUser = userRepository.findUserByDisplayName(user.getDisplayName());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
    // ###################################################################

    // ***** Validating the display name provided for a user, ensuring that it is not empty or null *****
    private void validateDisplayName(String displayName) {
        if (ObjectUtils.isEmpty(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    // ###################################################################

}