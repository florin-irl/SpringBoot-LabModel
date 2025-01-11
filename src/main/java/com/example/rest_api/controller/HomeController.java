package com.example.rest_api.controller;

import com.example.rest_api.users.database.repository.UserRepository;
import com.example.rest_api.users.database.model.UserEntity;
import com.example.rest_api.users.database.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public String home(Model model, Principal principal) {
        if (principal == null) {
            // Handle the case where Principal is null (user not authenticated)
            logger.error("Principal is null. Redirecting to login page.");
            return "redirect:/login";
        }

        String username;

        if (principal instanceof AuthenticatedUser authenticatedUser) {
            // Extract email if the principal is AuthenticatedUser
            username = authenticatedUser.getEmail();
        } else {
            // Fallback for other types of Principal
            username = principal.getName();
        }

        // Retrieve user from the database for additional info if needed
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(username);
        if (userEntityOptional.isEmpty()) {
            logger.error("User not found in the database for email: {}", username);
            return "redirect:/login"; // Redirect to login if the user is not found
        }

        UserEntity user = userEntityOptional.get();

        // Add user-specific attributes
        model.addAttribute("username", user.getUsername()); // Use username if available
        model.addAttribute("roles", user.getRoles()); // Add roles for display

        // Add navigation links for albums and photos
        model.addAttribute("albumsLink", "/albums"); // Link to the album list page
        model.addAttribute("createAlbumLink", "/albums/create"); // Link to the album creation page

        return "user/home";
    }
}
