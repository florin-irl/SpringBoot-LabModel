package com.example.rest_api.users.database.service;

import com.example.rest_api.users.database.model.PermissionEntity;
import com.example.rest_api.users.database.model.RoleEntity;
import com.example.rest_api.users.database.repository.PermissionRepository;
import com.example.rest_api.users.database.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    public Optional<RoleEntity> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Optional<RoleEntity> findById(Long roleId) {
        return roleRepository.findById(roleId);
    }

//    public void updateRolePermissions(Long roleId, List<Long> deletePermissionIds, Map<String, String> permissions, Map<String, String> newPermission) {
//        RoleEntity role = roleRepository.findById(roleId)
//                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
//
//        // Handle deletions
//        if (deletePermissionIds != null && !deletePermissionIds.isEmpty()) {
//            role.getPermissions().removeIf(permission -> deletePermissionIds.contains(permission.getId()));
//        }
//
//        // Handle updates
//        if (permissions != null && !permissions.isEmpty()) {
//            for (Map.Entry<String, String> entry : permissions.entrySet()) {
//                String[] parts = entry.getKey().split("\\.");
//                Long permissionId = Long.parseLong(parts[1]);
//
//                PermissionEntity permission = role.getPermissions().stream()
//                        .filter(p -> p.getId().equals(permissionId))
//                        .findFirst()
//                        .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
//
//                if ("httpMethod".equals(parts[2])) {
//                    permission.setHttpMethod(entry.getValue());
//                } else if ("url".equals(parts[2])) {
//                    permission.setUrl(entry.getValue());
//                }
//            }
//        }
//
//        // Handle additions
//        if (newPermission != null && !newPermission.isEmpty()) {
//            PermissionEntity permission = new PermissionEntity();
//            permission.setHttpMethod(newPermission.get("httpMethod"));
//            permission.setUrl(newPermission.get("url"));
//            permission.setRole(role);
//            role.getPermissions().add(permission);
//        }
//
//        roleRepository.save(role);
//    }


    public void deleteById(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    public Boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public RoleEntity save(RoleEntity admin) {
        return roleRepository.save(admin);
    }

    public void updateRolePermissions(Long roleId, List<Long> deletePermissionIds, String newHttpMethod, String newUrl) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        // Remove permissions
        if (deletePermissionIds != null) {
            for (Long permissionId : deletePermissionIds) {
                // Find and delete the permission from the database
                permissionRepository.deleteById(permissionId);
                role.getPermissions().removeIf(permission -> permission.getId().equals(permissionId));
            }
        }

        // Add a new permission
        if (newHttpMethod != null && newUrl != null) {
            PermissionEntity newPermission = new PermissionEntity();
            newPermission.setHttpMethod(newHttpMethod);
            newPermission.setUrl(newUrl);
            newPermission.setRole(role);
            role.getPermissions().add(newPermission);
        }

        roleRepository.save(role);
    }

}

