package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.Image;

import java.util.List;

public interface ImageService {
    List<Image> getAll();
    List<Image> getByTag(String tagName);
    Image getByTitle(String title);
    Image getByTitleWithJoin(String title);
    Image getById(Integer id);
    Image getByIdWithJoin(Integer title);
    void deleteById(Image image);
    void save(Image image);
    void update(Image image);
    void deleteByTitle(Image image);
}