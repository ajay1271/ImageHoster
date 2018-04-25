package com.upgrad.ImageHoster.common;

import com.google.common.hash.Hashing;
import com.upgrad.ImageHoster.model.User;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


@SuppressWarnings("unchecked")
public class UserManager extends SessionManager {

    /**
     * This method saves an User object into the database
     *
     * @param user the User object that we want to save into the database
     *
     * @return the User object that we saved
     */
    public User registerUser(final User user) {
        Session session = openSession();
        session.save(user);
        commitSession(session);
        return user;
    }

    /**
     * This method updates an User object into the database
     *
     * @param user the User object with the updated data
     */
    public void update(final User user) {
        Session session = openSession();
        session.update(user);
        commitSession(session);
    }

    /**
     * This method deletes an User object from the database
     *
     * @param user the User object to be deleted from the database
     */
    public void deleteUser(final User user) {
        Session session = openSession();
        session.delete(user);
        commitSession(session);
    }

    /**
     * This method retrieves an User object from the database using
     * the User object's username
     *
     * @param username the username of the User that we want to retrieve
     *
     * @return User object that we retrieved using the username or null
     * if the user is not found
     */
    public User getUserByName(final String username) {
        Session session = openSession();

        try {
            User user = (User)session.createCriteria(User.class)
                    .add(Restrictions.eq("username", username))
                    .uniqueResult();
            commitSession(session);

            return user;
        } catch(HibernateException e) {
            System.out.println("unable to retrieve an user from database by its username");
        }

        return null;
    }

    /**
     * This method retrieves an User object from the database using
     * the User object's username. In addition, it will do a join
     * on the ProfilePhoto table to retrieve the User's profile image
     *
     * @param username the username of the User that we want to retrieve
     *
     * @return User object that we retrieved using the username or null
     * if the user is not found
     */
    public User getUserByUsernameWithJoins(final String username) {
        Session session = openSession();

        try {
            User user = (User)session.createCriteria(User.class)
                    .add(Restrictions.eq("username", username))
                    .uniqueResult();
            Hibernate.initialize(user.getProfilePhoto()); // same as doing a join on profile image table
            commitSession(session);

            return user;
        } catch(HibernateException e) {
            System.out.println("unable to retrieve an image from database by its username with joins");
        }

        return null;
    }

    /**
     * This method retrieves an user from the database using the user's
     * username and password
     *
     * @param username the user's username
     * @param password the user's password
     *
     * @return the User object that we want to retrieve from the database.
     * Or null if username is not found in the database or the password doesn't
     * match the password of the user with the username.
     */
    public User loginUser(final String username, final String password) {
        Session session = openSession();
        User user = getUserByName(username);
        commitSession(session);

        // if a user cannot be found with username
        if (user == null)
            return null;

        String hashOfPassword = user.getPasswordHash();
        String hashOfEnteredPassword = Hashing.sha256()
                .hashString(password)
                .toString();

        // checks if the hash of the input parameter password
        // matches the password hash of the user found with username
        if (hashOfPassword.equals(hashOfEnteredPassword)) {
            return user;
        } else {
            return null;
        }
    }
}

