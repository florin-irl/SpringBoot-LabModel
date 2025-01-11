package com.example.rest_api.gallery.database.controller;


import com.example.rest_api.gallery.database.model.AlbumEntity;
import com.example.rest_api.gallery.database.model.PhotoEntity;
import com.example.rest_api.gallery.database.service.AlbumService;
import com.example.rest_api.gallery.database.service.PhotoService;
import com.example.rest_api.users.database.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final PhotoService photoService;
    private final RoleService roleService;

    @Autowired
    public AlbumController(AlbumService albumService, RoleService roleService, PhotoService photoService) {
        this.albumService = albumService;
        this.roleService = roleService;
        this.photoService = photoService;
    }

    // View Albums
    @GetMapping
    public String viewAlbums(Model model) {
        List<AlbumEntity> albums = albumService.findAll();
        model.addAttribute("albums", albums);
        return "albums/view-albums";
    }

    // Create Album Page
    @GetMapping("/create")
    public String createAlbumForm() {
        return "albums/create-album";
    }


    @PostMapping("/create")
    public String createAlbum(@RequestParam("albumName") String albumName,
                              @RequestParam("photos") List<MultipartFile> photos,
                              Principal principal) {
        String createdBy = principal.getName(); // Retrieve logged-in user's username or email
        albumService.createAlbum(albumName, photos, createdBy); // Pass photos along with other details
        return "redirect:/home"; // Properly redirect to the home page
    }

    @GetMapping("/{albumId}")
    public String viewAlbumContents(@PathVariable Long albumId, Model model) {
        AlbumEntity album = albumService.findAlbumWithPhotos(albumId);
        model.addAttribute("album", album);
        model.addAttribute("photos", album.getPhotos());
        return "albums/view";
    }

    @GetMapping("/photos/{photoId}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) {
        PhotoEntity photo = photoService.findById(photoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + photo.getName() + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo.getContent());
    }







}
