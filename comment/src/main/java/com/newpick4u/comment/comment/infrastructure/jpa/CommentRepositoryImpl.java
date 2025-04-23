package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.application.CommentSearchCriteria;
import com.newpick4u.comment.comment.application.dto.CommentListPageDto.CommentContentDto;
import com.newpick4u.comment.comment.application.dto.CommentWithGoodDto;
import com.newpick4u.comment.comment.domain.entity.Comment;
import com.newpick4u.comment.comment.domain.repository.CommentRepository;
import com.newpick4u.comment.global.common.CommonUtil;
import com.newpick4u.common.response.PageResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;
  private final CommentRepositoryCustom commentRepositoryCustom;

  @Override
  public Comment save(Comment comment) {
    return commentJpaRepository.save(comment);
  }

  @Override
  public Optional<Comment> findById(UUID commentId) {
    return commentJpaRepository.findById(commentId);
  }

  @Override
  public List<Comment> findAllByThreadIdAndDeletedAtIsNull(UUID threadId) {
    return commentJpaRepository.findAllByThreadIdAndDeletedAtIsNull(threadId);
  }

  @Override
  public PageResponse<CommentContentDto> findCommentsWithUserGood(
      Long userId,
      CommentSearchCriteria commentSearchCriteria) {
    Sort sortBy = getSortBy(commentSearchCriteria);
    Pageable pageable = getPageable(commentSearchCriteria, sortBy);

    Page<CommentWithGoodDto> page = commentRepositoryCustom.findCommentsWithUserGood(
        commentSearchCriteria.newsId(),
        commentSearchCriteria.threadId(),
        userId,
        pageable
    );

    return getCommentListPageResponse(page);
  }

  private PageResponse<CommentContentDto> getCommentListPageResponse(
      Page<CommentWithGoodDto> page) {
//    int currentPage = page.getNumber() + 1;
//    int size = page.getSize();
//    int totalPages = page.getTotalPages();
    long totalElements = page.getTotalElements();
    List<CommentContentDto> newContents = page.getContent()
        .stream()
        .map(commentWithGoodDto -> {
              return new CommentContentDto(
                  commentWithGoodDto.commentId(),
                  commentWithGoodDto.newsId(),
                  commentWithGoodDto.threadId(),
                  commentWithGoodDto.content(),
                  commentWithGoodDto.goodCount(),
                  commentWithGoodDto.goodId() != null,
                  CommonUtil.parseLDTToString(commentWithGoodDto.createdAt()),
                  CommonUtil.parseLDTToString(commentWithGoodDto.updatedAt())
              );
            }
        )
        .toList();
    Page<CommentContentDto> commentContentDtos = new PageImpl<CommentContentDto>(newContents,
        page.getPageable(), totalElements);

    return PageResponse.from(commentContentDtos);
  }

//  private CommentListPageDto getCommentListPageDto(Page<CommentWithGoodDto> page) {
//    int currentPage = page.getNumber() + 1;
//    int size = page.getSize();
//    long totalElements = page.getTotalElements();
//    int totalPages = page.getTotalPages();
//    List<CommentContentDto> newContents = page.getContent()
//        .stream()
//        .map(commentWithGoodDto -> {
//              return new CommentContentDto(
//                  commentWithGoodDto.commentId(),
//                  commentWithGoodDto.newsId(),
//                  commentWithGoodDto.threadId(),
//                  commentWithGoodDto.content(),
//                  commentWithGoodDto.goodCount(),
//                  commentWithGoodDto.goodId() != null,
//                  CommonUtil.parseLDTToString(commentWithGoodDto.createdAt()),
//                  CommonUtil.parseLDTToString(commentWithGoodDto.updatedAt())
//              );
//            }
//        )
//        .toList();
//    CommentListPageDto commentListPageDto = new CommentListPageDto(totalElements, totalPages,
//        currentPage, size, newContents);
//    return commentListPageDto;
//  }

  private Sort getSortBy(CommentSearchCriteria commentSearchCriteria) {
    Sort sortBy = Sort.by(commentSearchCriteria.sort().getFieldName());
    if (commentSearchCriteria.direction().name().equals(Sort.Direction.DESC.name())) {
      sortBy = sortBy.descending();
    } else {
      sortBy = sortBy.ascending();
    }
    return sortBy;
  }

  private Pageable getPageable(CommentSearchCriteria commentSearchCriteria, Sort sortBy) {
    Pageable pageable = PageRequest.of(
        commentSearchCriteria.page() - 1,
        commentSearchCriteria.size(),
        sortBy
    );
    return pageable;
  }
}
