package com.upgrad.ImageHoster.controller;


import com.google.common.hash.Hashing;
import com.upgrad.ImageHoster.model.ProfilePhoto;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.ProfilePhotoService;
import com.upgrad.ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Base64;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfilePhotoService profilePhotoService;

    /**
     * This controller method renders the user signup view
     *
     * @param session HTTP session to check if the user is logged in
     *
     * @return the user sign up view
     */
    @RequestMapping(value = "/signup")
    public String signUp(HttpSession session) {
        // If the user is not logged in, render the user sign up view
        if (session.getAttribute("currUser") == null){
            return "users/signup";
        } else {
            // return the user to the home page if the user has signed in
            return "redirect:/";
        }
    }

    /**
     * This controller method create a user
     *
     * @param username the username for the created user
     * @param password the password for the created user
     * @param session HTTP session for us to store the created user
     *
     * @return redircts to the homepage view
     */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUpUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                               HttpSession session) {
        // We'll first assign a default photo to the user
        ProfilePhoto photo = new ProfilePhoto();
        profilePhotoService.save(photo);

        // it is good security practice to store the hash version of the password
        // in the database. Therefore, if your a hacker gains access to your
        // database, the hacker cannot see the password for your users
        String passwordHash = hashPassword(password);
        User user = new User(username, passwordHash, photo);
        userService.register(user);

        // We want to create an "currUser" attribute in the HTTP session, and store the user
        // as the attribute's value to signify that the user has logged in
        session.setAttribute("currUser", user);

        return "redirect:/";
    }

    /**
     * This method render the user signin form
     *
     * @param session HTTP session to check if the user has logged in
     * @return
     */
    @RequestMapping(value = "/signin")
    public String signIn(HttpSession session) {
        // render the sign in view on only if the user is not logged in
        if (session.getAttribute("currUser") == null){
            return "users/signin";
        } else {
            return "redirect:/";
        }
    }

    /**
     * The controller method logs in an user
     * @param username the username of the user
     * @param password the password of the user
     * @param model used to pass data to the view for rendering. In this case,
     *              the model is used to pass errors back to the sign in view
     *              if there are errors
     * @param session HTTP session to store the signed in user
     *
     * @return the homepage view if signed in or the sign in view otherwise
     */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String signInUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             Model model,
                             HttpSession session) {
        // checks to see the a user exists with the given
        // username and password
        User user = userService.login(username, password);

        if (user != null ) {
            session.setAttribute("currUser", user);
            return "redirect:/";
        } else {
            // if a user cannot be found with the given username
            // and password, return a sign in error
            String error = "incorrect username or password";
            model.addAttribute("error", error);

            return "users/signin";
        }
    }

    /**
     * Signs out the current user
     *
     * @param session HTTP session that stores the current user
     *
     * @return redirect to the home page view
     */
    @RequestMapping(value = "/signout")
    public String signOut(HttpSession session) {
        // removes the "currUser" attribute from the session to
        // signify that the user has signed out
        session.removeAttribute("currUser");
        return "redirect:/";
    }

    /**
     * This controller method renders the user edit view
     *
     * @param session the HTTP session that tells us if the user is signed in
     * @param model used to pass data to the view for rendering
     *
     * @return the user profile edit view
     */
    @RequestMapping(value = "/user/edit_profile")
    public String editProfile(HttpSession session, Model model) {
        User currUser = (User)session.getAttribute("currUser");

        if(currUser == null ){
            return "redirect:/";
        }
        else {
            model.addAttribute("user", currUser);
            return "users/profile.html";
        }
    }

    /**
     * This controller method updates the user's profile
     *
     * @param description the updated description for the user
     * @param file the user profile image
     * @param session HTTP session

     * @return redirect to the home page
     *
     * @throws IOException
     */
    @RequestMapping(value = "/user/edit_profile", method = RequestMethod.POST)
    public String editUserProfile(@RequestParam("description") String description,
                                  @RequestParam("file") MultipartFile file,
                                  HttpSession session) throws IOException {
        User currUser = (User)session.getAttribute("currUser");

        // update photo data
        ProfilePhoto photo = currUser.getProfilePhoto();
        String photoDataAsBase64 = convertUploadedFileToBase64(file);
        photo.setprofileImageData(photoDataAsBase64);
        profilePhotoService.update(photo);

        // update user data
        currUser.setDescription(description);
        currUser.setProfilePhoto(photo);
        userService.update(currUser);

        return "redirect:/";
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
     * This is a helper function that encrypts a plain text password using the
     * SHA-256 encryption
     *
     * @param password the plain text String that we want to encrypt
     *
     * @return the SAH-256 encrypted version of the password
     */
    private String hashPassword(String password) {
        String passwordHash = Hashing.sha256()
                .hashString(password)
                .toString();

        return passwordHash;
    }
}