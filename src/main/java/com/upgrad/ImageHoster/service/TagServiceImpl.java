package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.common.TagManager;
import com.upgrad.ImageHoster.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private TagManager tagManager;

    public TagServiceImpl() {
        tagManager = new TagManager();
    }

    public List<Tag> getAll() {
        return tagManager.getAllTags();
    }

    public Tag getByName(String title) {
        return tagManager.findTag(title);
    }

    public Tag createTag(Tag tag) {
        return tagManager.createTag(tag);
    }
}
