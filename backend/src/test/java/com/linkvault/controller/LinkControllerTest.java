package com.linkvault.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.linkvault.model.Link;
import com.linkvault.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LinkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LinkRepository linkRepository;

  @BeforeEach
  void setUp() {
    linkRepository.deleteAll();
  }

  @Test
  void createsAndListsLinks() throws Exception {
    mockMvc.perform(post("/api/links")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "title": "Spring Boot",
                  "url": "https://spring.io/projects/spring-boot",
                  "tags": "backend,java",
                  "description": "Backend framework",
                  "personalNote": "Use this when checking REST controller examples."
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value("Spring Boot"))
        .andExpect(jsonPath("$.tags").value("backend,java"))
        .andExpect(jsonPath("$.personalNote").value("Use this when checking REST controller examples."));

    mockMvc.perform(get("/api/links"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Spring Boot"))
        .andExpect(jsonPath("$[0].tags").value("backend,java"))
        .andExpect(jsonPath("$[0].personalNote").value("Use this when checking REST controller examples."));
  }

  @Test
  void autoFetchesTitleWhenBlank() throws Exception {
    mockMvc.perform(post("/api/links")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "url": "https://google.com"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").exists());
  }

  @Test
  void rejectsNonHttpUrls() throws Exception {
    mockMvc.perform(post("/api/links")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "url": "file:///etc/passwd"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void listsNewestLinksFirst() throws Exception {
    Link olderLink = new Link();
    olderLink.setTitle("Older docs");
    olderLink.setUrl("https://example.com/older");
    linkRepository.saveAndFlush(olderLink);

    Thread.sleep(10);

    Link newerLink = new Link();
    newerLink.setTitle("Newer docs");
    newerLink.setUrl("https://example.com/newer");
    linkRepository.saveAndFlush(newerLink);

    mockMvc.perform(get("/api/links"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Newer docs"))
        .andExpect(jsonPath("$[1].title").value("Older docs"));
  }

  @Test
  void togglesFavouriteStatus() throws Exception {
    Link link = new Link();
    link.setTitle("Favourite Link");
    link.setUrl("https://example.com");
    Link savedLink = linkRepository.save(link);

    mockMvc.perform(patch("/api/links/{id}/favourite", savedLink.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.favourite").value(true));

    mockMvc.perform(patch("/api/links/{id}/favourite", savedLink.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.favourite").value(false));
  }

  @Test
  void deletesLinks() throws Exception {
    Link link = new Link();
    link.setTitle("Docs");
    link.setUrl("https://example.com/docs");
    Link savedLink = linkRepository.save(link);

    mockMvc.perform(delete("/api/links/{id}", savedLink.getId()))
        .andExpect(status().isNoContent());

    assertThat(linkRepository.existsById(savedLink.getId())).isFalse();
  }
}
