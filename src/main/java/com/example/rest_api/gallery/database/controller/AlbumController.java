package com.example.rest_api.gallery.database.controller;


import com.example.rest_api.gallery.database.model.AlbumEntity;
import com.example.rest_api.gallery.database.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/create")
    public String createAlbumForm(Model model) {
        model.addAttribute("album", new AlbumEntity());
        return "albums/create";
    }

    @PostMapping("/create")
    public String createAlbum(@ModelAttribute("album") AlbumEntity album, Principal principal) {
        String createdBy = principal.getName(); // Retrieve logged-in user's username or email
        albumService.createAlbum(album.getName(), createdBy);
        return "redirect:/albums";
    }

    @GetMapping()
    public String listAlbums(Model model, Principal principal) {
        String createdBy = principal.getName();
        model.addAttribute("albums", albumService.getAlbumsByUser(createdBy));
        return "albums/list";
    }

    @GetMapping("/{albumId}")
    public String viewAlbum(@PathVariable Long albumId, Model model) {
        AlbumEntity album = albumService.getAlbumById(albumId);
        model.addAttribute("album", album);
        return "albums/view";
    }
}
