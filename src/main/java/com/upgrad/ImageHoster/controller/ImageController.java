package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.model.Tag;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.ImageService;
import com.upgrad.ImageHoster.service.TagService;
import com.upgrad.ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;


@Controller
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    /**
     * This controller method returns all the images that have been
     * uploaded to the website
     * @param model used to pass data to the view for rendering
     *
     * @return the homepage view
     */
    @RequestMapping("/")
    public String listImages(Model model) {
        List<Image> images = imageService.getAll();
        model.addAttribute("images", images);

        return "home";
    }

    /**
     * This controller method renders an image upload form
     *
     * @param session a HTTP session that tells us if the current user
     *                is logged in
     *
     * @return the image upload form view
     */
    @RequestMapping("/images/upload")
    public String uploadImage(HttpSession session) {
        // retrieves the current user from the HTTP session
        User currUser = (User) session.getAttribute("currUser");

        // currUser is null means that the user is not logged in
        // therefore redirect the user back to the home page
        if(currUser == null ){
            return "redirect:/";
        }
        else {
            return "images/upload";
        }
    }

    /**
     * This controller method retrieves the data that the user entered
     * into the image uploader form, and creates an image
     *
     * @param title title of the uploaded image
     * @param description description of the uploaded image
     * @param file the image to be uploaded
     * @param tags tags (i.e. categories) for the images
     * @param session HTTP session that tells us if the user is logged in
     *
     * @return view for the uploaded image
     *
     * @throws IOException
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("tags") String tags,
                         HttpSession session) throws IOException {
        User currUser = (User) session.getAttribute("currUser");

        // if the user is not logged in, redirect to the home page
        if(currUser == null ){
            return "redirect:/";
        }
        else {
            List<Tag> imageTags = findOrCreateTags(tags);
            String uploadedImageData = convertUploadedFileToBase64(file);

            Image newImage = new Image(title, description, uploadedImageData, currUser, imageTags);
            imageService.save(newImage);

            return "redirect:/images/" + newImage.getTitle();
        }
    }

    /**
     * This controller shows a specific image
     * @param title the title of the image that we want to retrieve
     * @param model used to pass data to the view for rendering
     *
     * @return view for the image that was requested
     */
    @RequestMapping("/images/{title}")
    public String showImage(@PathVariable String title, Model model) {
        Image image = imageService.getByTitleWithJoin(title);
        image.setNumView(image.getNumView() + 1);
        imageService.update(image);

        model.addAttribute("user", image.getUser());
        model.addAttribute("image", image);
        model.addAttribute("tags", image.getTags());

        return "images/image";
    }

    /**
     * This method deletes a specific image from the database
     *
     * @param title title of the image that we want to delete
     *
     * @return redirects the user to the homepage view
     */
    @RequestMapping("/images/{title}/delete")
    public String deleteImage(@PathVariable String title) {
        Image image = imageService.getByTitle(title);
        imageService.deleteByTitle(image);


        return "redirect:/";
    }

    /**
     * This controller method displays an image edit form, so the user
     * can update the image's description and uploaded file
     *
     * @param title title of the image that we want to edit
     * @param model used to pass data to the view for rendering
     *
     * @return the image edit form view
     */
    @RequestMapping("/images/{title}/edit")
    public String editImage(@PathVariable String title, Model model) {
        Image image = imageService.getByTitleWithJoin(title);
        String tags = convertTagsToString(image.getTags());

        model.addAttribute("image", image);
        model.addAttribute("tags", tags);

        return "images/edit";
    }

    /**
     * This controller method updates the image that we wanted to edit
     *
     * @param title title of the image that we want to edit
     * @param description the updated description for the image
     * @param file the updated file for the image
     * @param tags the updated tags for the image
     *
     * @return the view for the updated image
     *
     * @throws IOException
     */
    @RequestMapping(value = "/editImage", method = RequestMethod.POST)
    public String edit(@RequestParam("title") String title,
                       @RequestParam("description") String description,
                       @RequestParam("file") MultipartFile file,
                       @RequestParam("tags") String tags) throws IOException {
        Image image = imageService.getByTitle(title);
        List<Tag> imageTags = findOrCreateTags(tags);
        String updatedImageData = convertUploadedFileToBase64(file);

        image.setDescription(description);
        image.setImageFile(updatedImageData);
        image.setTags(imageTags);
        imageService.update(image);

        return "redirect:/images/" + title;
    }

    /**
     * This help function converts an image file to base64 encoding, which
     * makes it easier for us to store the image data in the datbase
     *
     * @param file the file that we want to convert to base64 encoding
     *
     * @return base64 encoding of the file that was passed into this function
     *
     * @throws IOException
     */
    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    /**
     * This is a helper function that checks if a tag has been created and
     * saved into the DB. If the tag has not saved DB, this function
     * will create the tag
     *
     * @param tagNames a String that represents the tags, and tagNames
     *                 can be comma delimited to represent multiple Tags
     *                 i.e. "cat, young, kitty" to represent the tags:
     *                 "cat", "young", and "kitty"
     *
     * @return Tag objects stored in a List
     */
    private List<Tag> findOrCreateTags(String tagNames) {
        // converts a comma delimited String into a String Tokenizer
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        // for each token in the Tokenizer
        while(st.hasMoreTokens()) {
            // trim any white spaces before or after the String token
            String tagName = st.nextToken().trim();
            // check if the associated Tag has been created and stored in the database
            Tag tag = tagService.getByName(tagName);

            // if the associated Tag has not been created, create the tag
            // and store it in the database
            if(tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }

            tags.add(tag);
        }

        return tags;
    }

    /**
     * This method takes a List of Tag objects and convert it back
     * to its comma delimited String. This method is needed for the
     * image edit form
     *
     * @param tags a List of Tag objects
     *
     * @return A comma delimited, String version of the Tag objects
     */
    private String convertTagsToString (List<Tag> tags) {
        String tagString = "";

        for(int i = 0; i <= tags.size() - 2; i++) {
            tagString += tags.get(i).getName() + ", ";
        }

        Tag lastTag = tags.get(tags.size() - 1);
        tagString += lastTag.getName();

        return tagString;
    }
}
