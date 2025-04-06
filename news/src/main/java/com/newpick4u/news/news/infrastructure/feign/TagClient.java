package com.newpick4u.news.news.infrastructure.feign;

import com.newpick4u.news.news.infrastructure.feign.dto.TagDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tag-service")
public interface TagClient {

    @GetMapping ("/api/v1/tags/list")
    List<TagDto> getOrCreateTags(@RequestBody List<String> tagNames);
}
