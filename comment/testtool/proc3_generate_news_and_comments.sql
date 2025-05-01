-- Step 3: 뉴스별 댓글 및 좋아요 생성

DROP PROCEDURE IF EXISTS newpick4u.proc_generate_news_and_comments;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_news_and_comments()
BEGIN
    -- 변수 선언
    DECLARE v_news_id BINARY(16);
    DECLARE v_comment_id BINARY(16);
    DECLARE v_now DATETIME(6);
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_loop_comment INT;
    DECLARE v_loop_good INT;
    DECLARE v_random_user_id BIGINT;

    -- 커서 선언
    DECLARE cur_news CURSOR FOR
        SELECT news_id FROM newpick4u.p_news;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    -- 임시 user id 저장 테이블
    DROP TEMPORARY TABLE IF EXISTS tmp_user_ids;
    CREATE TEMPORARY TABLE tmp_user_ids
    (
        user_id BIGINT NOT NULL
    );

    INSERT INTO tmp_user_ids (user_id)
    SELECT user_id
    FROM newpick4u.p_user;

    SET v_now = NOW(6);

    -- 커서 오픈
    OPEN cur_news;

    read_loop:
    LOOP
        FETCH cur_news INTO v_news_id;

        IF v_done = 1 THEN
            LEAVE read_loop;
        END IF;

        -- 댓글 110개 생성
        SET v_loop_comment = 0;
        WHILE v_loop_comment < 110
            DO
                SET v_comment_id = UUID_TO_BIN(UUID());

                -- 랜덤 user_id 선택
                SELECT user_id
                INTO v_random_user_id
                FROM tmp_user_ids
                ORDER BY RAND()
                LIMIT 1;

                -- created_by 값을 삽입
                INSERT INTO newpick4u.p_comment (comment_id,
                                                 news_id,
                                                 content,
                                                 good_count,
                                                 created_at,
                                                 created_by)
                VALUES (v_comment_id,
                        v_news_id,
                        CONCAT('Sample Comment Content ', v_loop_comment),
                        30,
                        v_now,
                        v_random_user_id);

                -- 댓글 1개당 좋아요 30개 생성
                SET v_loop_good = 0;
                WHILE v_loop_good < 30
                    DO
                        SELECT user_id
                        INTO v_random_user_id
                        FROM tmp_user_ids
                        ORDER BY RAND()
                        LIMIT 1;

                        INSERT INTO newpick4u.p_comment_good (comment_id,
                                                              user_id)
                        VALUES (v_comment_id,
                                v_random_user_id);
                        SET v_loop_good = v_loop_good + 1;
                    END WHILE;

                SET v_loop_comment = v_loop_comment + 1;
            END WHILE;

        COMMIT;
    END LOOP;

    -- 커서 닫기
    CLOSE cur_news;

    -- 임시 테이블 정리
    DROP TEMPORARY TABLE IF EXISTS tmp_user_ids;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_news_and_comments();
