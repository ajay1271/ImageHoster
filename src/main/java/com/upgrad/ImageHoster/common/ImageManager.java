package com.upgrad.ImageHoster.common;

import com.upgrad.ImageHoster.model.Image;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import java.util.List;

@SuppressWarnings("unchecked")
public class ImageManager extends SessionManager {
    /**
     * This method retrieves all of the images saved in the database
     *
     * @return a List of Image objects
     */
    public List<Image> getAllImages() {
        Session session = openSession();
        List<Image> images = session.createCriteria(Image.class).list();
        commitSession(session);

        return images;
    }

    /**
     * This method retrieves an image by its title
     *
     * @param title the title of the image that we are looking for
     *
     * @return an Image object that we retrieved by its title
     */
    public Image getImageByTitle(final String title) {
        Session session = openSession();

        try {
            Image image = (Image)session.createCriteria(Image.class)
                    .add(Restrictions.eq("title", title))
                    .uniqueResult(); // retrieves only 1 image
            commitSession(session);

            return image;
        } catch(HibernateException e) {
            System.out.println("unable to retrieve an image from database by its title");
        }

        return null;
    }

    /**
     * This method retrieves an image by its title, as well as the data
     * related to its tags, user, and user's profile photo
     *
     * @param title the title of the image that we are looking for
     *
     * @return an Image object that we retrieved by its title
     */
    public Image getImageByTitleWithJoins(final String title) {
        Session session = openSession();

        try {
            Image image = (Image)session.createCriteria(Image.class)
                    .add(Restrictions.eq("title", title))
                    .uniqueResult();
            Hibernate.initialize(image.getTags()); // doing a join on tags table
            Hibernate.initialize(image.getUser()); // doing a join on user table
            Hibernate.initialize(image.getUser().getProfilePhoto()); // doing a join on profile photo table
            commitSession(session);

            return image;
        } catch(HibernateException e) {
            System.out.println("unable to retrieve an image from database by its title");
        }

        return null;
    }

    /**
     * This method retrieves an image by a specific tag.
     *
     * @param tagName the tag that we want to retrieve images by
     *
     * @return a list of Image objects that we retrieved by its tag
     */
    public List<Image> getImagesByTag(final String tagName) {
        Session session = openSession();

        try {
            List<Image> images = session
                    .createCriteria(Image.class)
                    .createAlias("tags", "tags")
                    .add(Restrictions.eq("tags.name", tagName))
                    .list();

            commitSession(session);

            return images;
        } catch(HibernateException e) {
            System.out.println("unable to retrieve an image from database by its title");
        }

        return null;
    }

    /**
     * This method retrieves the number of images stored in the database
     *
     * @return the number of images stored in the database
     */
    public long getNumberOfImages() {
        Session session = openSession();

        // to learn more about Hibernate Projection:
        // https://stackoverflow.com/questions/7498205/when-to-use-hibernate-projections
        Long numImages = (Long) session.createCriteria(Image.class).setProjection(Projections.rowCount()).uniqueResult();
        commitSession(session);

        return numImages;
    }

    /**
     * This method deletes an image data from the database
     *
     * @param title the title of the image that we want to delete
     */
    public void deleteImage(final String title) {
        Session session = openSession();
        Query query = session.createQuery("Delete from " + Image.class.getName() + " where title=:imageTitle");
        query.setParameter("imageTitle", title);
        query.executeUpdate();
        commitSession(session);
    }

    /**
     * This method saves an image's data to the database
     *
     * @param image the Image who's data that we want to save to the database
     */
    public void saveImage(final Image image) {
        Session session = openSession();
        session.save(image);
        commitSession(session);
    }

    /**
     * This method updates an image's data in the database
     *
     * @param updatedImage an Image object with the updated data
     */
    public void updateImage(final Image updatedImage) {
        Session session = openSession();
        session.update(updatedImage);
        commitSession(session);
    }
}