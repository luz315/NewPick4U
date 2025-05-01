package com.newpick4u.comment.comment.application.dto;

public record GetUserResponseDto(
    Long userId,
    String username,
    String name,
    String role
) {

}