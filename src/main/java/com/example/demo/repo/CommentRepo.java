package com.example.demo.repo;

import com.example.demo.models.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comments,Integer> {
    Page<Comments>findAllByMovieId(PageRequest pageRequest, Integer movieId);
}
