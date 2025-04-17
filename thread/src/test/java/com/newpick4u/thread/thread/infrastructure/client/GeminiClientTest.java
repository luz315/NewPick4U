package com.newpick4u.thread.thread.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class GeminiClientTest {

  @Autowired
  private GeminiClient geminiClient;

  @MockitoBean
  private CommentClientImpl commentClient;

  @Test
  void givenComments_whenAnalyzeSummary_thenReturnsValidSummary() {
    // given
    UUID threadId = UUID.randomUUID();
    UUID threadId2 = UUID.randomUUID();
    UUID threadId3 = UUID.randomUUID();
    UUID threadId4 = UUID.randomUUID();
    UUID threadId5 = UUID.randomUUID();

    List<String> commentList = new ArrayList<>();
    List<String> commentList2 = new ArrayList<>();
    List<String> commentList3 = new ArrayList<>();
    List<String> commentList4 = new ArrayList<>();
    List<String> commentList5 = new ArrayList<>();

    for (int i = 0; i < 10000; i++) {
      commentList.add("이 정책은 정말 좋은 것 같습니다." + i);
      commentList2.add("이 정책은 정말 좋은 것 같습니다." + i);
      commentList3.add("이 정책은 정말 좋은 것 같습니다." + i);
      commentList4.add("이 정책은 정말 좋은 것 같습니다." + i);
      commentList5.add("이 정책은 정말 좋은 것 같습니다." + i);
    }

//    List<CommentResponse> comments = List.of(
//        new CommentResponse(threadId, "이 정책은 정말 좋은 것 같습니다."),
//        new CommentResponse(threadId, "더 많은 정보가 필요해요."),
//        new CommentResponse(threadId, "사람들 반응이 갈리네요.")
//    );

    // when
    String result = geminiClient.analyzeSummary(threadId, commentList);
    String result1 = geminiClient.analyzeSummary(threadId2, commentList2);
    String result2 = geminiClient.analyzeSummary(threadId3, commentList3);
    String result3 = geminiClient.analyzeSummary(threadId4, commentList4);
    String result4 = geminiClient.analyzeSummary(threadId5, commentList5);

    // then
    assertNotNull(result);
    System.out.println("[Gemini 요약 결과 0] " + result);
    System.out.println("[Gemini 요약 결과 1] " + result1);
    System.out.println("[Gemini 요약 결과 2] " + result2);
    System.out.println("[Gemini 요약 결과 3] " + result3);
    System.out.println("[Gemini 요약 결과 4] " + result4);

//   [Gemini 요약 결과] 제공된 댓글들을 종합해 볼 때, 현재 여론은 다음과 같이 요약될 수 있습니다:
//
//*   **긍정적인 시각:** 정책에 대해 긍정적으로 평가하는 의견이 존재합니다. "이 정책은 정말 좋은 것 같습니다."라는 댓글이 이를 뒷받침합니다.
//*   **정보 부족:** 정책에 대한 충분한 정보를 얻지 못해 판단을 유보하는 의견도 있습니다. "더 많은 정보가 필요해요."라는 댓글은 정책에 대한 이해를 높이기 위한 추가적인 정보 제공의 필요성을 강조합니다.
//*   **의견 분분:** 정책에 대한 찬반 의견이 엇갈리고 있습니다. "사람들 반응이 갈리네요."라는 댓글은 정책에 대한 다양한 시각이 존재하며, 합의점을 찾기 어려울 수 있음을 시사합니다.
//
//    따라서, 현재 여론은 긍정적인 시각과 함께 정보 부족으로 인한 유보적 태도, 그리고 찬반 양론이 혼재된 상황으로 볼 수 있습니다. 정책의 성공적인 정착을 위해서는 긍정적인 측면을 강화하고, 정보 부족을 해소하며, 다양한 의견을 수렴하는 노력이 필요할 것으로 보입니다.

  }

}
