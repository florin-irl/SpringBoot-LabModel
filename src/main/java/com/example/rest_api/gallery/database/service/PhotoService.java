package com.example.rest_api.gallery.database.service;

import com.example.rest_api.gallery.database.model.AlbumEntity;
import com.example.rest_api.gallery.database.model.PhotoEntity;
import com.example.rest_api.gallery.database.repository.AlbumRepository;
import com.example.rest_api.gallery.database.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoService implements IPhotoService{
    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, AlbumRepository albumRepository) {
        this.photoRepository = photoRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional
    public PhotoEntity addPhotoToAlbum(Long albumId, String name, byte[] content) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found!"));

        PhotoEntity photo = new PhotoEntity();
        photo.setName(name);
        photo.setContent(content);
        photo.setAlbum(album);

        return photoRepository.save(photo);
    }

    @Override
    @Transactional
    public PhotoEntity updatePhotoName(Long photoId, String newName) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found!"));
        photo.setName(newName);
        return photoRepository.save(photo);
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        if(!photoRepository.existsById(photoId)){
            throw new IllegalArgumentException("Photo not found!");
        }
        photoRepository.deleteById(photoId);
    }

    @Override
    public java.util.List<PhotoEntity> getPhotosByAlbum(Long albumId) {
        return photoRepository.findAllByAlbumId(albumId);
    }

}
