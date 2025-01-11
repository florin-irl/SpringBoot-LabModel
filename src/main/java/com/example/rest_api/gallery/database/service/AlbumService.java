package com.example.rest_api.gallery.database.service;

import com.example.rest_api.gallery.database.model.AlbumEntity;
import com.example.rest_api.gallery.database.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService implements IAlbumService{

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional
    public AlbumEntity createAlbum(String name,
                                   String createdBy){
        if(albumRepository.existsByName(name)){
            throw new IllegalArgumentException("Album with name " + name + " already exists");
        }

        AlbumEntity album = new AlbumEntity();
        album.setName(name);
        album.setCreatedBy(createdBy);

        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public AlbumEntity updateAlbumName(Long albumId, String newName) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found!"));
        album.setName(newName);
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public void deleteAlbum(Long albumId){
        if(!albumRepository.existsById(albumId)){
            throw new IllegalArgumentException("Album not found!");
        }
        albumRepository.deleteById(albumId);
    }

    @Override
    public java.util.List<AlbumEntity> getAlbumsByUser(String createdBy){
        return albumRepository.findAllByCreatedBy(createdBy);
    }

    @Override
    public AlbumEntity getAlbumById(Long albumId){
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found!"));
    }
}
