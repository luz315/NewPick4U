package com.newpick4u.news.global.exception;

import com.newpick4u.common.exception.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NewsErrorCode implements ErrorCode {

    NEWS_NOT_FOUND(40401, "요청한 뉴스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_NEWS(40901, "이미 등록된 뉴스입니다.", HttpStatus.CONFLICT),
    TAG_LIMIT_EXCEEDED(40001, "뉴스 태그는 최대 10개까지 가능합니다.", HttpStatus.BAD_REQUEST),
    TAG_INBOX_SERIALIZATION_FAIL(50001, "태그 인박스 직렬화 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_SCAN_FAIL(50002, "Redis SCAN 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    VECTOR_GENERATION_FAIL(50003, "뉴스 벡터 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    KAFKA_NEWS_SAVE_FAIL(50004, "Kafka 뉴스 저장 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    KAFKA_TAG_SAVE_FAIL(50005, "Kafka 태그 저장 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    TEST_SIMULATED_FAILURE_ONCE(50006, "테스트용 첫 번째 실패 유도", HttpStatus.INTERNAL_SERVER_ERROR),
    TEST_SIMULATED_FAILURE_ALWAYS(50007, "테스트용 무조건 실패 유도", HttpStatus.INTERNAL_SERVER_ERROR),
    VECTOR_LENGTH_MISMATCH(40002, "벡터 길이가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
