package com.newpick4u.tag.presentation.controller;

import com.newpick4u.tag.application.usecase.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

//  @GetMapping("/search")
//  public ResponseEntity<?> searchTags(
//      @RequestParam(required = false) String tagName,
//      @RequestParam(required = false) Long minScore,
//      @RequestParam(required = false) Long maxScore,
//      Pageable pageable
//  ) {
//    tagService.searchTags()
//  }
}
