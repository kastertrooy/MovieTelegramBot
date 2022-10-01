package com.example.demo.servise;

import com.example.demo.models.Comments;
import com.example.demo.repo.CommentRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepo commentRepo;
    public CommentService(CommentRepo commentRepo){
        this.commentRepo = commentRepo;
    }

    public Page<Comments> getAll(PageRequest pageRequest, Integer id) {
        return commentRepo.findAllByMovieId(pageRequest,id);
    }
}
