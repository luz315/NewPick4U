package com.newpick4u.comment.comment.domain.criteria;

public record CommentSearchCriteria(
    Integer size,
    Integer page,
    String sort,
    String orderby
) {

}
