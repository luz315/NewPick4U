package com.newpick4u.thread.thread.application.usecase;

import java.util.List;
import java.util.UUID;

public interface AiClient {

  String analyzeSummary(UUID uuid, List<String> commentList);
}
