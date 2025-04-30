package com.example.socialmedia.service;

import com.example.socialmedia.Utils.JwtUtils;
import com.example.socialmedia.controller.rq.comment.CommentCreateRq;
import com.example.socialmedia.controller.rq.comment.CommentQueryRq;
import com.example.socialmedia.dao.CommentDao;
import com.example.socialmedia.dao.UserDao;
import com.example.socialmedia.dto.comment.CommentCreateDto;
import com.example.socialmedia.dto.comment.CommentDto;
import com.example.socialmedia.dto.comment.CommentQueryDto;
import com.example.socialmedia.entity.CommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDao commentDao;
    private final UserDao userDao;
    private final JwtUtils jwtUtils;

    public CommentDto create(CommentCreateRq commentCreateRq){
        // 驗證使用者是否有帶合法的 Token
        jwtUtils.validateToken();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .postId(commentCreateRq.getPostId())
                .userId(jwtUtils.getLoginUserId())
                .content(commentCreateRq.getContent())
                .parentId(commentCreateRq.getParentId())
                .build();

        CommentEntity newData = commentDao.createComment(commentCreateDto);

        return CommentDto.builder()
                .id(newData.getId())
                .userName(userDao.findUserById(newData.getUserId()).getUserName())
                .postId(newData.getPostId())
                .content(newData.getContent())
                .parentId(newData.getParentId())
                .build();
    }

    public List<CommentDto> query(CommentQueryRq commentQueryRq){
        // 驗證使用者是否有帶合法的 Token
        jwtUtils.validateToken();

        CommentQueryDto commentQueryDto = CommentQueryDto.builder()
                .postId(commentQueryRq.getPostId())
                .build();

        List<CommentEntity> allCommentData = commentDao.queryAllCommentsInPost(commentQueryDto);

        return allCommentData.stream()
                .sorted(Comparator.comparing(CommentEntity::getId)) // 由最近到最遠
                .map(this::commentEntityToDto)
                .toList();
    }

    public CommentDto commentEntityToDto(CommentEntity commentEntity){
        return CommentDto.builder()
                .id(commentEntity.getId())
                .postId(commentEntity.getPostId())
                .userName(userDao.findUserById(commentEntity.getUserId()).getUserName())
                .content(commentEntity.getContent())
                .parentId(commentEntity.getParentId())
                .build();
    }
}
