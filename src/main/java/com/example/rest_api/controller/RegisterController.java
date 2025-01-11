package com.example.rest_api.controller;

import com.example.rest_api.users.database.model.Role;
import com.example.rest_api.users.database.model.RoleEntity;
import com.example.rest_api.users.database.model.UserEntity;
import com.example.rest_api.users.database.service.RoleService;
import com.example.rest_api.users.database.service.UserService;
import com.example.rest_api.users.database.service.UserValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private UserService userService;
    private RoleService roleService;
    private UserValidatorService userValidatorService;
    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    public RegisterController(UserService userService, RoleService roleService, UserValidatorService userValidatorService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userValidatorService = userValidatorService;
    }

    @GetMapping()
    public String loadRegisterPage(Model model){
        model.addAttribute("user", new UserEntity());
        return "authentication/register";
    }

    @PostMapping()
    public String register(@ModelAttribute("user") UserEntity user, BindingResult bindingResult){
        userValidatorService.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "authentication/register";

        if (userService.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered!");
        }

        user.setEmail(user.getEmail());
        user.setUsername(user.getUsername());
        user.setIsOAuthAccount(false); // Classic registration

        Optional<RoleEntity> optionalRole = roleService.findByName(Role.DEFAULT.name());
        optionalRole.ifPresent(user::addRole);

        userService.save(user);

        return "redirect:/login";
    }
}
