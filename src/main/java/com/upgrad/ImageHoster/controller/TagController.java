package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class TagController {
    @Autowired
    ImageService imageService;

    /**
     * This controller method renders the view of images that are tagged by
     * a specific "tag"
     *
     * @param tagName the tag that we want to retrieve images by
     * @param model Model injected by Spring
     *
     * @return the tag/images view
     */
    @RequestMapping("/tags/{tagName}")
    public String showImage(@PathVariable String tagName,
                            Model model) {
        List<Image> images = imageService.getByTag(tagName);

        model.addAttribute("images", images);
        model.addAttribute("tag", tagName);

        return "tag/images";
    }
}
