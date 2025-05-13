-- Step 2: 뉴스 2,000개 생성
DROP PROCEDURE IF EXISTS newpick4u.proc_generate_news_data;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_news_data()
BEGIN
    DECLARE v_loop_count INT DEFAULT 0;
    DECLARE v_news_origin_id BINARY(16);
    DECLARE v_ai_news_id BINARY(16);
    DECLARE v_news_id BINARY(16);
    DECLARE v_now DATETIME(6);

    SET v_now = NOW(6);

    WHILE v_loop_count < 5
        DO
            -- Step 1: news_origin 생성
            SET v_news_origin_id = UUID_TO_BIN(UUID());

            INSERT INTO newpick4u.p_news_origin (news_origin_id,
                                                 created_at,
                                                 updated_at,
                                                 news_published_date,
                                                 title,
                                                 url,
                                                 status)
            VALUES (v_news_origin_id,
                    v_now,
                    v_now,
                    v_now,
                    CONCAT('Origin Title ', v_loop_count),
                    CONCAT('https://origin.example.com/', v_loop_count),
                    'SENDED');

            -- Step 2: ai_news 생성 (news_origin_id 참조)
            SET v_ai_news_id = UUID_TO_BIN(UUID());

            INSERT INTO newpick4u.p_ai_news (ai_news_id,
                                             origin_news_id,
                                             created_at,
                                             updated_at,
                                             published_date,
                                             title,
                                             url,
                                             keywords,
                                             original_response,
                                             summary)
            VALUES (v_ai_news_id,
                    v_news_origin_id,
                    v_now,
                    v_now,
                    DATE_FORMAT(v_now, '%Y-%m-%d %H:%i:%s'),
                    CONCAT('AI Title ', v_loop_count),
                    CONCAT('https://ai.example.com/', v_loop_count),
                    'keyword1, keyword2',
                    'Original response sample.',
                    'Summary sample.');

            -- Step 3: news 생성 (ai_news_id 참조)
            SET v_news_id = UUID_TO_BIN(UUID());

            INSERT INTO newpick4u.p_news (news_id,
                                          ai_news_id,
                                          created_at,
                                          updated_at,
                                          published_date,
                                          title,
                                          url,
                                          content,
                                          status)
            VALUES (v_news_id,
                    LOWER(CONCAT(
                            SUBSTRING(HEX(v_ai_news_id), 1, 8), '-',
                            SUBSTRING(HEX(v_ai_news_id), 9, 4), '-',
                            SUBSTRING(HEX(v_ai_news_id), 13, 4), '-',
                            SUBSTRING(HEX(v_ai_news_id), 17, 4), '-',
                            SUBSTRING(HEX(v_ai_news_id), 21)
                          )), -- ai_news_id는 UUID 포맷 문자열로 변환하여 소문자로 저장
                    v_now,
                    v_now,
                    DATE_FORMAT(v_now, '%Y-%m-%d %H:%i:%s'),
                    CONCAT('News Title ', v_loop_count),
                    CONCAT('https://news.example.com/', v_loop_count),
                    'Sample content for news article.',
                    'ACTIVE');

            -- Loop Counter 증가
            SET v_loop_count = v_loop_count + 1;
        END WHILE;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_news_data();

