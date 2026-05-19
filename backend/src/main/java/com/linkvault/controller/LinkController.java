package com.linkvault.controller;

import com.linkvault.model.Link;
import com.linkvault.repository.LinkRepository;
import com.linkvault.service.TitleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/links")
public class LinkController {

  private final LinkRepository linkRepository;
  private final TitleService titleService;

  public LinkController(LinkRepository linkRepository, TitleService titleService) {
    this.linkRepository = linkRepository;
    this.titleService = titleService;
  }

  @GetMapping
  public List<Link> listLinks() {
    return linkRepository.findAllByOrderByCreatedAtDesc();
  }

  @PostMapping
  public ResponseEntity<Link> createLink(@Valid @RequestBody Link link) {
    if (link.getTitle() == null || link.getTitle().isBlank()) {
      String fetchedTitle = titleService.fetchTitle(link.getUrl());
      if (fetchedTitle != null && !fetchedTitle.isBlank()) {
        link.setTitle(fetchedTitle);
      } else {
        link.setTitle(link.getUrl());
      }
    }

    Link savedLink = linkRepository.save(link);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedLink.getId())
        .toUri();

    return ResponseEntity.created(location).body(savedLink);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
    if (!linkRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    linkRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
