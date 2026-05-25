package com.linkvault.controller;

import com.linkvault.model.Link;
import com.linkvault.repository.LinkRepository;
import com.linkvault.service.TitleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "*")
@RestController
@Validated
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
    String url = link.getUrl();
    if (link.getTitle() == null || link.getTitle().isBlank()) {
      String fetchedTitle = titleService.fetchTitle(url);
      if (fetchedTitle != null && !fetchedTitle.isBlank()) {
        link.setTitle(fetchedTitle);
      } else {
        link.setTitle(url);
      }
    }

    Link savedLink = linkRepository.save(link);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedLink.getId())
        .toUri();

    return ResponseEntity.created(location).body(savedLink);
  }

  @PatchMapping("/{id}/favourite")
  public ResponseEntity<Link> toggleFavourite(@PathVariable Long id) {
    return linkRepository.findById(id)
        .map(link -> {
          link.setFavourite(!link.isFavourite());
          return ResponseEntity.ok(linkRepository.save(link));
        })
        .orElse(ResponseEntity.notFound().build());
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
