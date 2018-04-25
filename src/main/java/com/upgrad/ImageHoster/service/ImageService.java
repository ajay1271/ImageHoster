package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.Image;

import java.util.List;

public interface ImageService{
    List<Image> getAll();
    List<Image> getByTag(String tagName);
    Image getByTitle(String title);
    Image getByTitleWithJoin(String title);
    void deleteByTitle(Image image);
    void save(Image image);
    void update(Image image);
}