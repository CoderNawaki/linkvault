package com.linkvault.repository;

import com.linkvault.model.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {

  List<Link> findAllByOrderByCreatedAtDesc();
}
