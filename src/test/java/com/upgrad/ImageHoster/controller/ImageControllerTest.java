package com.upgrad.ImageHoster.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.model.ProfilePhoto;
import com.upgrad.ImageHoster.model.Tag;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.ImageService;
import com.upgrad.ImageHoster.service.TagService;
import com.upgrad.ImageHoster.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(ImageController.class)
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @MockBean
    private TagService tagService;

    @MockBean
    private UserService userService;

    protected MockHttpSession session;

    @Test
    public void shouldRenderHomePageAsVisitor() throws Exception {
        // checks to see if the returned view contains the string "Sign in"
        this.mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Sign in")));
    }

    @Test
    public void shouldRenderHomePageAsLoggedInUser() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // checks to see if the returned view contains the string "Sign out"
        this.mockMvc.perform(get("/").session(session))
                .andExpect(content().string(containsString("Sign out")));
    }

    @Test
    public void shouldRenderHomePageWithOneImage() throws Exception {
        // create a mock image
        Image image = new Image();
        image.setTitle("This is an image");
        image.setUploadDate(LocalDate.now());

        // setup the mock imageService to return the mock image as a List<Image>;
        Mockito.when(imageService.getAll()).thenReturn(Arrays.asList(image));

        // checks to see if the returned view contains the title of the image
        this.mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("This is an image")));
    }

    @Test
    public void shouldRenderRequestedImage() throws Exception {
        // create a new profile image
        ProfilePhoto photo = new ProfilePhoto();

        // create a new user
        User user = new User();
        user.setProfilePhoto(photo);
        user.setUsername("My Username");

        // create a mock image
        Image image = new Image();
        image.setUser(user);
        image.setTitle("This is an image");
        image.setUploadDate(LocalDate.now());

        // setup the mock imageService to return the mock image as a List<Image>;
        Mockito.when(imageService.getByTitleWithJoin(Mockito.anyString())).thenReturn(image);

        // checks to see if the returned view contains the title of the image
        this.mockMvc.perform(get("/images/someImageTitle"))
                .andExpect(content().string(containsString("This is an image")))
                .andExpect(content().string(containsString("My Username")));
    }


    @Test
    public void shouldRenderUploadPage() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // checks to see if the returned view contains the string "Upload a new image"
        this.mockMvc.perform(get("/images/upload").session(session))
                .andExpect(content().string(containsString("Upload a new image")));
    }

    @Test
    public void shouldUploadImageOk() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // creates a mock file to simulate an uploaded file
        MockMultipartFile mockImage = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some image".getBytes());

        // create a mock tag string
        String tags = "tag1, tag2";

        // checks to see if we redirect to the correct URL once the image has been uploaded
        this.mockMvc.perform(multipart("/upload")
                .file(mockImage)
                .session(session)
                .param("title", "someImageTitle")
                .param("description", "description")
                .param("tags", tags))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/images/someImageTitle"));
    }

    @Test
    public void shouldDeleteImageOk() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // setup the mock imageService to return an Image when trying
        // to retrieve an image by some image title;
        Mockito.when(imageService.getByTitle(Mockito.anyString())).thenReturn(new Image());

        // checks to see if we are redirected to the home page once we delete an image
        this.mockMvc.perform(get("/images/someImageTitle/delete").session(session))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void shouldRenderEditPage() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // creates mock Tags
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("tag1"));
        tags.add(new Tag("tag2"));

        // creats a mock Image
        Image image = new Image();
        image.setTitle("title");
        image.setDescription("description");
        image.setTags(tags);

        // setup the mock imageService to return the mock image when
        // trying to retrieve an image by its title
        Mockito.when(imageService.getByTitleWithJoin(Mockito.anyString())).thenReturn(image);

        // checks to see if the returned HTML contains:
        // 1) Edit Image
        // 2) the image's title
        // 3) the image's descriptions
        this.mockMvc.perform(get("/images/someImageTitle/edit").session(session))
                .andExpect(content().string(containsString("Edit image")))
                .andExpect(content().string(containsString(image.getTitle())))
                .andExpect(content().string(containsString(image.getDescription())));

    }

    @Test
    public void shouldEditImageOk() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // creates a mock uploaded file
        MockMultipartFile mockImage = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some image".getBytes());

        // create a mock tag string
        String tags = "tag1, tag2";

        // setup the mock imageService to return the mock image when trying to retreive
        // an image by its title
        Mockito.when(imageService.getByTitleWithJoin(Mockito.anyString())).thenReturn(new Image());

        // checks to see if we redirect to the URL of the edited image
        this.mockMvc.perform(multipart("/upload")
                .file(mockImage)
                .session(session)
                .param("title", "someImageTitle")
                .param("description", "description")
                .param("tags", tags))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/images/someImageTitle"));
    }
}
