package com.example.rest_api.controller;

import com.example.rest_api.users.database.model.RoleEntity;
import com.example.rest_api.users.database.model.UserEntity;
import com.example.rest_api.users.database.service.RoleService;
import com.example.rest_api.users.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("welcomeMessage", "Hello Admin");
        return "admin/dashboard";
    }

    // User Management
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    // Update Roles Page
    @GetMapping("/users/{userId}/roles")
    public String updateUserRolesForm(@PathVariable Long userId, Model model) {
        UserEntity user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<RoleEntity> allRoles = roleService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("roles", allRoles);
        return "admin/update-roles";
    }

    // Handle Role Updates
    @PostMapping("/users/{userId}/roles")
    public String updateUserRoles(@PathVariable Long userId, @RequestParam(required = false) List<Long> roleIds) {
        // If no roles are selected, default to an empty list
        if (roleIds == null) {
            roleIds = List.of(); // Empty list to clear all roles
        }
        userService.updateUserRoles(userId, roleIds);
        return "redirect:/admin/users";
    }

    // Role Management Page
    @GetMapping("/roles")
    public String roleManagement(Model model) {
        List<RoleEntity> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        return "admin/roles";
    }

    // Delete Role
    @PostMapping("/roles/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId) {
        roleService.deleteById(roleId);
        return "redirect:/admin/roles";
    }

    // Edit Role Page
    @GetMapping("/roles/{roleId}/edit")
    public String editRole(@PathVariable Long roleId, Model model) {
        RoleEntity role = roleService.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        model.addAttribute("role", role);
        return "admin/edit-role";
    }

    // Update Role Permissions
    @PostMapping("/roles/{roleId}/edit")
    public String updateRolePermissions(@PathVariable Long roleId,
                                        @RequestParam(required = false) List<Long> deletePermissionIds,
                                        @RequestParam(required = false) String newHttpMethod,
                                        @RequestParam(required = false) String newUrl) {
        roleService.updateRolePermissions(roleId, deletePermissionIds, newHttpMethod, newUrl);
        return "redirect:/admin/roles";
    }


}
