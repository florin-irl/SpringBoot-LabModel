package com.example.rest_api.gallery.database.repository;

import com.example.rest_api.gallery.database.model.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {

    //Find all photos by name
    List<PhotoEntity> findAllByName(String name);

    //Find all photos in a specific album
    List<PhotoEntity> findAllByAlbumId(Long albumId);

    //Update Photo Name
    @Modifying
    @Query(value = "update photo set name=:name WHERE id=:id", nativeQuery = true)
    void updatePhotoName(Long id, String name);

    // Delete all photos by album ID
    @Modifying
    @Query(value = "DELETE FROM photo WHERE album_id=:albumId", nativeQuery = true)
    void deleteAllByAlbumId(Long albumId);

    // Delete a photo by ID
    @Modifying
    @Query(value = "DELETE FROM photo WHERE id=:id", nativeQuery = true)
    void deletePhotoById(Long id);
}
