package com.example.socialmedia.dao;

import com.example.socialmedia.dto.comment.CommentCreateDto;
import com.example.socialmedia.dto.comment.CommentQueryDto;
import com.example.socialmedia.entity.CommentEntity;
import com.example.socialmedia.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentDao {
    private final CommentRepository commentRepository;

    public CommentEntity createComment(CommentCreateDto commentCreateDto){
        CommentEntity commentData = new CommentEntity();

        commentData.setUserId(commentCreateDto.getUserId());
        commentData.setPostId(commentCreateDto.getPostId());
        commentData.setParentId(commentCreateDto.getParentId());
        commentData.setContent(commentCreateDto.getContent());

        return commentRepository.save(commentData);
    }

    public List<CommentEntity> queryAllCommentsInPost(CommentQueryDto commentQueryDto){
        return commentRepository.findByPostId(commentQueryDto.getPostId());
    }
}
