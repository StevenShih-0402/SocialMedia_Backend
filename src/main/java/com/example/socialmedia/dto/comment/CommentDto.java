package com.example.socialmedia.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String userName;
    private Integer postId;
    private Integer parentId;
    private String content;
}
