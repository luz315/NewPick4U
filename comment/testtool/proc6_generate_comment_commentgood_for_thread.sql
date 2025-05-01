-- 기존 프로시저가 존재하면 삭제
DROP PROCEDURE IF EXISTS newpick4u.proc_generate_comment_commentgood_for_thread;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_comment_commentgood_for_thread()
BEGIN
    DECLARE v_thread_id BINARY(16);
    DECLARE v_comment_id BINARY(16);
    DECLARE v_user_id BIGINT;
    DECLARE v_comment_good_id BIGINT;
    DECLARE v_now DATETIME(6);
    DECLARE v_done INT DEFAULT 0;

    -- Cursor 선언
    DECLARE thread_cursor CURSOR FOR
        SELECT thread_id FROM newpick4u.p_thread;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    -- thread 순회
    OPEN thread_cursor;
    thread_loop:
    LOOP
        FETCH thread_cursor INTO v_thread_id;
        IF v_done = 1 THEN
            LEAVE thread_loop;
        END IF;

        -- thread마다 댓글 1000개 생성
        SET @i = 0;
        WHILE @i < 110
            DO
                SET v_now = NOW(6);
                SET v_comment_id = UUID_TO_BIN(UUID(), TRUE); -- TRUE: MySQL 내부 최적화 포맷
                SET v_user_id = FLOOR(1 + RAND() * 1000); -- 1 ~ 1000번 유저 중 랜덤 선택

                INSERT INTO newpick4u.p_comment (comment_id,
                                                 thread_id,
                                                 news_id,
                                                 content,
                                                 created_at,
                                                 created_by,
                                                 updated_at,
                                                 updated_by,
                                                 good_count)
                VALUES (v_comment_id,
                        v_thread_id,
                        NULL, -- 뉴스 귀속 아님
                        CONCAT('Comment for Thread ', HEX(v_thread_id), ' - ', @i),
                        v_now,
                        v_user_id,
                        v_now,
                        v_user_id,
                        30);

                -- 댓글마다 좋아요 100개 생성
                SET @j = 0;
                WHILE @j < 30
                    DO
                        SET v_comment_good_id = NULL; -- AUTO_INCREMENT라 null 넣으면 됨
                        SET v_user_id = FLOOR(1 + RAND() * 1000); -- 좋아요 유저 랜덤

                        INSERT INTO newpick4u.p_comment_good (user_id,
                                                              comment_id)
                        VALUES (v_user_id,
                                v_comment_id);

                        SET @j = @j + 1;
                    END WHILE;

                SET @i = @i + 1;
            END WHILE;
    END LOOP;
    CLOSE thread_cursor;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_comment_commentgood_for_thread();
