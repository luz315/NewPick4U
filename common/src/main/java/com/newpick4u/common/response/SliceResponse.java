package com.newpick4u.common.response;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Builder
public record SliceResponse<T>(
    List<T> contents,
    boolean hasNext,
    int pageNumber,
    int pageSize
) {

  public static <T> SliceResponse<T> from(Slice<T> slice) {
    Pageable pageable = slice.getPageable();

    return SliceResponse.<T>builder()
        .contents(slice.getContent())
        .hasNext(slice.hasNext())
        .pageNumber(pageable.getPageNumber() + 1) // 1부터 시작하도록
        .pageSize(pageable.getPageSize())
        .build();
  }

  private static <U, T> SliceResponse<U> from(SliceResponse<T> sliceResponse, List<U> list) {
    return SliceResponse.<U>builder()
        .contents(list)
        .hasNext(sliceResponse.hasNext())
        .pageNumber(sliceResponse.pageNumber())
        .pageSize(sliceResponse.pageSize())
        .build();
  }

  public <U> SliceResponse<U> map(Function<T, U> mapper) {
    return SliceResponse.from(this, this.contents.<T>stream()
        .map(mapper)
        .toList());
  }
}
