package com.example.rest_api.gallery.database.service;

import com.example.rest_api.gallery.database.model.PhotoEntity;

import java.util.List;

public interface IPhotoService {
    PhotoEntity addPhotoToAlbum(Long albumId, String name, byte[] content);
    PhotoEntity updatePhotoName(Long photoId, String newName);
    void deletePhoto(Long photoId);
    List<PhotoEntity> getPhotosByAlbum(Long albumId);
    PhotoEntity findById(Long photoId);
}
