package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.ProfilePhoto;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.ProfilePhotoService;
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
import org.testng.Assert;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProfilePhotoService profilePhotoService;

    protected MockHttpSession session;


    @Test
    public void shouldRenderSignupPageAsVisitor() throws Exception {
        // checks to see if the returned HTML contains the string "Sign up for an account"
        this.mockMvc.perform(get("/signup"))
                .andExpect(content().string(containsString("Sign up for an account")));
    }

    @Test
    public void shouldRedirectSignupPageIfLoggedIn() throws Exception {
        // adds a user to the mock session to simulate that an user has signed in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // checks to see if the user is redirected to home page when trying to
        // access the sign in page if he or she is signed in
        this.mockMvc.perform(get("/signup").session(session))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void shouldDisplayErrorInSignupPage() throws Exception {
        // checks to see if the view contains username and password errors
        // when trying to sign up with a username and password that's shorter
        // than 6 characteres
        this.mockMvc.perform(post("/signup")
                .param("username", "a")
                .param("password", "a"))
                .andExpect(content().string(containsString("needs to be 6 characters or longer")))
                .andExpect(content().string(containsString("needs to be 6 characters or longer")));
    }

    @Test
    public void shouldDisplayErrorIfUsernameHasBeenRegisteredInSignupPage() throws Exception {
        Mockito.when(userService.getByName(Mockito.anyString())).thenReturn(new User());

        // checks to see if the view contains username errors when trying to sign up
        // with an username that hs been previously registered
        this.mockMvc.perform(post("/signup")
                .param("username", "username")
                .param("password", "12345678"))
                .andExpect(content().string(containsString("username has been registered")));
    }

    @Test
    public void shouldRedirectToHomePageAfterSignUp() throws Exception {
        // checks to see if user can register ok with the proper username
        // and password
        this.mockMvc.perform(post("/signup")
                .param("username", "abcdefg")
                .param("password", "12345678"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void shouldRenderSigninPageAsVisitor() throws Exception {
        // checks to see if the view contains the string "Sign up for an account"
        this.mockMvc.perform(get("/signin"))
                .andExpect(content().string(containsString("Sign into your account")));
    }

    @Test
    public void shouldRedirectSigninPageIfLoggedIn() throws Exception {
        // adds a user to the mock session to simulate that an user has logged in
        User user = new User();
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // checks to see if the user is redirected to home page when trying
        // to access the sign in page
        this.mockMvc.perform(get("/signin").session(session))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void shouldDisplayErrorInSigninPage() throws Exception {
        Mockito.when(userService.getByName(Mockito.anyString())).thenReturn(null);

        // checks to see if the view returns an error message if trying to
        // sign in with incorrect username or password
        this.mockMvc.perform(post("/signin")
                .param("username", "username")
                .param("password", "12345678"))
                .andExpect(content().string(containsString("Incorrect username or password")));
    }

    @Test
    public void shouldDirectToHomePageIfSignin() throws Exception {
        User user = new User();
        Mockito.when(userService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(user);

        // checks to see if the user is redirected to the home page after
        // signing in with the correct password and username
        HttpSession session = this.mockMvc.perform(post("/signin")
                .param("username", "username")
                .param("password", "12345678"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession();

        Assert.assertEquals(user, session.getAttribute("currUser"));
    }

    @Test
    public void shouldRenderEditPage() throws Exception {
        // adds a user to the mock session to simulate that the user has signed in
        String description = "description";
        String username = "username";
        User user = new User();
        user.setDescription(description);
        user.setUsername(username);

        session = new MockHttpSession();
        session.setAttribute("currUser", user);


        // checks to see if the edit user view contains the username
        // and description of the user
        this.mockMvc.perform(get("/user/edit_profile").session(session))
                .andExpect(content().string(containsString(username)))
                .andExpect(content().string(containsString(description)));
    }

    @Test
    public void shouldEditUserOk() throws Exception {
        // creates a mock Profile Photo
        ProfilePhoto photo = new ProfilePhoto();

        // creates a mock user
        String description = "description";
        String username = "username";
        User user = new User();
        user.setDescription(description);
        user.setUsername(username);
        user.setProfilePhoto(photo);

        // adds a user to the mock session
        session = new MockHttpSession();
        session.setAttribute("currUser", user);

        // creates a mock file to simulate a new uploaded profile image
        MockMultipartFile mockImage = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some image".getBytes());

        // checks to see if we are redirected to the homepage if the user's profile
        // is updated successfully
        this.mockMvc.perform(multipart("/user/edit_profile")
                .file(mockImage)
                .session(session)
                .param("description", "description"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
    }
}
