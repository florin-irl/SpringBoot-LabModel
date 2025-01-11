package com.example.rest_api.gallery.database.repository;

import com.example.rest_api.gallery.database.model.AlbumEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {

    //Find all albums by name
    List<AlbumEntity> findAllByName(String name);

    //Find albums created by a specific user
    List<AlbumEntity> findAllByCreatedBy(String createdBy);

    //Update Album Name
    @Modifying
    @Query(value = "update album set name=:name WHERE id=:id", nativeQuery = true)
    void updateAlbumName(Long id, String name);

    //Check if an album with the given name exists
    Boolean existsByName(String name);

    //Delete an album by ID
    @Modifying
    @Query(value = "delete from album where id=:id", nativeQuery = true)
    void deleteAlbumById(Long id);

    @Query("SELECT a FROM AlbumEntity a LEFT JOIN FETCH a.photos WHERE a.id = :albumId")
    Optional<AlbumEntity> findByIdWithPhotos(@Param("albumId") Long albumId);

}