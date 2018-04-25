package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.service.ImageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(TagController.class)
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    public void shouldRenderTagPageWithImage() throws Exception {
        // create a mock image
        Image image = new Image();
        image.setTitle("This is an image");
        image.setUploadDate(LocalDate.now());

        // setup the mock imageService to return the mock image when retrieving
        // an image by its tag;
        Mockito.when(imageService.getByTag(Mockito.anyString())).thenReturn(Arrays.asList(image));

        // checks to see if the returned view contains the title of the mock image
        this.mockMvc.perform(get("/tags/someTag"))
                .andExpect(content().string(containsString("This is an image")));
    }
}
