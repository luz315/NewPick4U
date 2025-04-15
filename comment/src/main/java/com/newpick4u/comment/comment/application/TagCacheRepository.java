package com.newpick4u.comment.comment.application;

import java.util.List;

public interface TagCacheRepository {

  void increaseTagCount(List<String> tags);
}
