package com.newpick4u.ainews.ainews.infrastructure.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class GeminiClientTest {

  @Autowired
  GeminiClient geminiClient;

  @Test
  @DisplayName("gemini 호출 테스트")
  void callGeminiClientTest() {
    String target = """
        테슬라의 올해 1분기 차량 인도 실적이 월가 전망치를 크게 밑돌면서 실적 하향 조정이 이어지고 있다고 블룸버그통신이 5일(이하 현지시각) 보도했다. <br />
        <br />
        특히 일론 머스크 테슬라 최고경영자(CEO)의 정치적 행보가 소비자 반발을 부른 ‘브랜드 훼손’이 주된 원인으로 지목됐다.<br />
        <div style="margin:30px 0;text-align:center;"><!-- [글로벌]_PC_기사내 --><ins class="adsbygoogle"     style="display:block"     data-ad-client="ca-pub-2676875859063566"     data-ad-slot="4796558345"     data-ad-format="auto"     data-full-width-responsive="true"></ins><script>     (adsbygoogle = window.adsbygoogle || []).push({});</script></div>
        JP모건체이스의 라이언 브링크먼 애널리스트는 전날 낸 보고서에서 "테슬라의 1분기 인도 실적은 우리가 예상했던 최악의 수치보다도 낮았다"며 "소비자 반응의 강도를 과소평가했을 가능성이 있다"고 분석했다. <br />
        <br />
        그는 테슬라의 1분기 주당순이익(EPS) 전망치를 기존 40센트에서 36센트로 하향 조정했다. 이는 블룸버그가 집계한 시장 예상치 평균(46센트)에도 미치지 못하는 수준이다.<br />
        <br />
        이같은 실적 부진은 주가에도 즉각 반영됐다. 테슬라 주가는 장 초반 4% 이상 급락했으며 지난해 12월 17일 사상 최고가 대비 44% 하락한 상태다.<br />
        <br />
        브링크먼은 올해 전체 EPS 전망치 역시 2.30달러로 낮춰 잡았다. 이는 지난 1월 테슬라의 분기 실적 발표 이후 17%나 하락한 시장 평균 전망치(2.70달러)보다 낮다.<br />
        <br />
        테슬라는 올해 1분기 동안 총 33만6681대를 인도했는데 이는 지난 2022년 이후 가장 저조한 분기 실적이다. 회사는 생산라인 전환과 함께 모델Y의 리디자인에 집중하고 있지만 정치적 논란에 휘말린 머스크 CEO의 영향도 적지 않은 것으로 분석된다.<br />
        <br />
        머스크는 지난 1월부터 도널드 트럼프 미국 대통령이 신설한 정부효율부를 이끌면서 정치적 중립성을 사실상 포기했다. 그는 올해 유럽에서도 극우 정치 세력과 연대하는 발언을 이어가며 논란을 빚었고 그 여파로 테슬라의 독일 내 판매량은 전년 동기 대비 62% 급감했다. 독일은 테슬라가 유럽에서 유일하게 완성차 공장을 운영하는 국가다.<br />
        <br />
        블룸버그는 머스크가 ‘특별 정부 직원’’ 신분으로 트럼프 행정부에 기여하고 있으며 법적으로 연간 130일만 근무할 수 있기 때문에 곧 이 자리에서 물러날 예정이라고 전했다. 다만 백악관 법률자문실이 그의 근무 일수를 검토 중이며 공식 퇴임일은 아직 정해지지 않은 것으로 알려졌다. 정부효율부에서 나온 뒤에도 머스크는 트럼프 대통령의 핵심 경제 고문으로서 영향력을 유지할 것으로 알려졌다.<br />김현철 글로벌이코노믹 기자 rock@g-enews.com
        """;

    // 불필요한 API 호출을 막기위한 주석처리
//    ProceedAiNewsDto proceedAiNewsDto = geminiClient.processByAiApi(target);
//    log.info("originalString = {}", proceedAiNewsDto.originalString());
//    log.info("summary = {}", proceedAiNewsDto.proceedFields().summary());
//    log.info("tags = {}", proceedAiNewsDto.proceedFields().tags());
  }
}