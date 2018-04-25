package com.upgrad.ImageHoster.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Tag")
public class Tag {
    // These annotations auto-increments the id column for us whenever
    // a new Tag is stored into the database
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    // this annotation completes the many-to-many declaration created
    // in the Image class
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private List<Image> images;


    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
