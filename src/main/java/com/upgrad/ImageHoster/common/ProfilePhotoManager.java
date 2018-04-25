package com.upgrad.ImageHoster.common;

import com.upgrad.ImageHoster.model.ProfilePhoto;
import org.hibernate.Session;

@SuppressWarnings("unchecked")
public class ProfilePhotoManager extends SessionManager {
    /**
     * This method saves a ProfilePhoto into the database
     *
     * @param photo the ProfilePhoto that we want to save
     */
    public void saveProfilePhoto(final ProfilePhoto photo) {
        Session session = openSession();
        session.save(photo);
        commitSession(session);
    }

    /**
     * This method updates a ProfilePhoto in the database
     *
     * @param updatedphoto a ProfilePhoto object with the updated data
     */
    public void updateProfilePhoto(final ProfilePhoto updatedphoto) {
        Session session = openSession();
        session.update(updatedphoto);
        commitSession(session);
    }
}
