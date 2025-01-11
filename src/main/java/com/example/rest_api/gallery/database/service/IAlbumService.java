package com.example.rest_api.gallery.database.service;

import com.example.rest_api.gallery.database.model.AlbumEntity;

import java.util.List;

public interface IAlbumService {
    AlbumEntity createAlbum(String name, String createdBy);
    AlbumEntity updateAlbumName(Long albumId, String newName);
    void deleteAlbum(Long albumId);
    List<AlbumEntity> getAlbumsByUser(String createdBy);
    AlbumEntity getAlbumById(Long albumId);
}

