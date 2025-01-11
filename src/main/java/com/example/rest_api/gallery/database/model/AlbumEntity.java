package com.example.rest_api.gallery.database.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "album")
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "created_by", nullable = false)
    private String createdBy; // Stores the creator's unique identifier (e.g., username or email)

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PhotoEntity> photos = new ArrayList<>();

    // Utility Methods
    public void addPhoto(PhotoEntity photo) {
        photos.add(photo);
        photo.setAlbum(this);
    }

    public void removePhoto(PhotoEntity photo) {
        photos.remove(photo);
        photo.setAlbum(null);
    }
}
