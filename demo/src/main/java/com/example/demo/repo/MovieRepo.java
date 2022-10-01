package com.example.demo.repo;

import com.example.demo.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepo extends JpaRepository<Movie,Integer> {
   // Page<Movie>findAll(PageRequest pageRequest);
    List<Movie> findAllByDeleteAtIsNull();
}
