package com.example.socialmedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "COMMENTS")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @Id
    @Column(name = "COMMENT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_gen")
    @SequenceGenerator(name = "comment_seq_gen", sequenceName = "SEQ_COMMENTS", allocationSize = 1)
    private Integer id;

    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "POST_ID")
    private Integer postId;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "PARENT_ID")
    private Integer parentId;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
