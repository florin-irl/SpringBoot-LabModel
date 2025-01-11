package com.example.rest_api.gallery.database.controller;


import com.example.rest_api.gallery.database.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/upload/{albumId}")
    public String uploadPhotoForm(@PathVariable Long albumId, Model model) {
        model.addAttribute("albumId", albumId);
        return "photos/upload";
    }

    @PostMapping("/upload/{albumId}")
    public String uploadPhoto(@PathVariable Long albumId,
                              @RequestParam("photo") MultipartFile photoFile,
                              @RequestParam("name") String name) throws IOException {
        byte[] content = photoFile.getBytes();
        photoService.addPhotoToAlbum(albumId, name, content);
        return "redirect:/albums/" + albumId;
    }
}
