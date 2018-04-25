package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.ProfilePhoto;

public interface ProfilePhotoService {
    void save(ProfilePhoto image);
    void update(ProfilePhoto image);

}
