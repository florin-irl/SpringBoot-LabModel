package com.example.rest_api.gallery.database.service;

import com.example.rest_api.gallery.database.model.AlbumEntity;
import com.example.rest_api.gallery.database.model.PhotoEntity;
import com.example.rest_api.gallery.database.repository.AlbumRepository;
import com.example.rest_api.gallery.database.repository.PhotoRepository;
import com.example.rest_api.users.database.model.PermissionEntity;
import com.example.rest_api.users.database.model.RoleEntity;
import com.example.rest_api.users.database.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, PhotoRepository photoRepository, RoleRepository roleRepository) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
        this.roleRepository = roleRepository;
    }

    public List<AlbumEntity> findAll() {
        return albumRepository.findAll();
    }

    public void createAlbum(String albumName, List<MultipartFile> photos, String createdBy) {
        // Create and save the album
        AlbumEntity album = new AlbumEntity();
        album.setName(albumName);
        album.setCreatedBy(createdBy); // Assuming your AlbumEntity has a `createdBy` field
        album = albumRepository.save(album);

        // Save photos
        List<PhotoEntity> photoEntities = new ArrayList<>();
        for (MultipartFile photo : photos) {
            try {
                PhotoEntity photoEntity = new PhotoEntity();
                photoEntity.setName(photo.getOriginalFilename());
                photoEntity.setContent(photo.getBytes());
                photoEntity.setAlbum(album);
                photoEntities.add(photoEntity);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process photo", e);
            }
        }
        photoRepository.saveAll(photoEntities);

        // Create roles and permissions
        createAlbumRoles(albumName);
    }

    private void createAlbumRoles(String albumName) {
        String basePath = "/album/" + albumName.toLowerCase() + "/**";

        // Create CARS_ALBUM role
        RoleEntity role = new RoleEntity();
        role.setName(albumName.toUpperCase() + "_ALBUM");

        PermissionEntity permission = new PermissionEntity();
        permission.setHttpMethod("GET");
        permission.setUrl(basePath);
        permission.setRole(role);

        role.setPermissions(List.of(permission));
        roleRepository.save(role);

        // Create CARS_ALBUM_ADMIN role
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(albumName.toUpperCase() + "_ALBUM_ADMIN");

        PermissionEntity getPermission = new PermissionEntity();
        getPermission.setHttpMethod("GET");
        getPermission.setUrl(basePath);
        getPermission.setRole(adminRole);

        PermissionEntity postPermission = new PermissionEntity();
        postPermission.setHttpMethod("POST");
        postPermission.setUrl(basePath);
        postPermission.setRole(adminRole);

        PermissionEntity deletePermission = new PermissionEntity();
        deletePermission.setHttpMethod("DELETE");
        deletePermission.setUrl(basePath);
        deletePermission.setRole(adminRole);

        List<PermissionEntity> adminPermissions = List.of(getPermission, postPermission, deletePermission);
        adminRole.setPermissions(adminPermissions);

        roleRepository.save(adminRole);
    }

    public Optional<AlbumEntity> findById(Long albumId) {
        return albumRepository.findById(albumId);
    }

    public AlbumEntity findAlbumWithPhotos(Long albumId) {
        return albumRepository.findByIdWithPhotos(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
    }





}
