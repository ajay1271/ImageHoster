//package com.upgrad.ImageHoster.controller;
//
//import com.upgrad.ImageHoster.model.Image;
//import com.upgrad.ImageHoster.model.User;
//import com.upgrad.ImageHoster.service.CommentService;
//import com.upgrad.ImageHoster.service.ImageService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(CommentController.class)
//public class CommentControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CommentService commentService;
//
//    @MockBean
//    private ImageService imageService;
//
//    protected MockHttpSession session;
//
//    @Test
//    public void shouldCreateAComment() throws Exception {
//        // adds a user to the mock session, to simulate that a user has signed in
//        User user = new User();
//        session = new MockHttpSession();
//        session.setAttribute("currUser", user);
//
//        // create a mock image
//        Image image = new Image();
//        image.setTitle("title");
//        Mockito.when(imageService.getById(Mockito.anyInt())).thenReturn(image);
//
//        // checks to see if we are redirected to the URL of the image that we added a comment to
//        this.mockMvc.perform(post("/image/" + image.getId() + "/" + image.getTitle() + "/comments/create")
//                .session(session)
//                .param("comment", "some comment"))
//                .andExpect(status().is(302))
//                .andExpect(redirectedUrl("/images/" + image.getId() + "/" + image.getTitle()));
//    }
//
//}
