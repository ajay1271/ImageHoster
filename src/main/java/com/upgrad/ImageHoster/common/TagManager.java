package com.upgrad.ImageHoster.common;

import com.upgrad.ImageHoster.model.Tag;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

@SuppressWarnings("unchecked")
public class TagManager extends SessionManager {

    /**
     * This method saves a Tag object to the database
     *
     * @param tag Tag object to be saved into the database
     *
     * @return the saved Tag object
     */
    public Tag createTag(Tag tag) {
        Session session = openSession();
        session.save(tag);
        commitSession(session);
        return tag;
    }

    /**
     * This method checks if a tag exists in the database
     *
     * @param tagName the name of the Tag that we are looking for
     *
     * @return a Tag object
     */
    public Tag findTag(String tagName) {
        Session session = openSession();

        Criteria criteria = session.createCriteria(Tag.class);
        Tag tag = (Tag) criteria
                .add(Restrictions.eq("name", tagName))
                .uniqueResult();

        return tag;
    }

    /**
     * This returns all the Tags in the database
     *
     * @return List of Tag objects
     */
    public List<Tag> getAllTags() {
        Session session = openSession();
        List<Tag> tags = session.createCriteria(Tag.class).list();
        commitSession(session);

        return tags;
    }
}
