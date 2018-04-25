package com.upgrad.ImageHoster.common;

import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.model.ProfilePhoto;
import com.upgrad.ImageHoster.model.Tag;
import com.upgrad.ImageHoster.model.User;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ManagedSessionContext;

public class SessionManager {

    /**
     * This method creates a database session
     *
     * @return Database session
     */
    public Session openSession() {

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Image.class);
        configuration.addAnnotatedClass(Tag.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(ProfilePhoto.class);

        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        ManagedSessionContext.bind(session);
        session.beginTransaction();

        return session;
    }

    /**
     * This method commits the session into the database
     *
     * @return void
     */
    public void commitSession(final Session session){
        session.flush();
        session.getTransaction().commit();
        session.close();
    }
}
