package com.example.socialmedia.controller.rq.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRq {

    @NotNull
    private Integer postId;

    private Integer parentId;

    @NotBlank
    @Size(max = 200)
    private String content;
}
