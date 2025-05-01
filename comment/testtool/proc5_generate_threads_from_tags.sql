DROP PROCEDURE IF EXISTS newpick4u.proc_generate_threads_from_tags;

DELIMITER $$

-- Step 5: 쓰레드 생성
CREATE PROCEDURE newpick4u.proc_generate_threads_from_tags()
BEGIN
    DECLARE v_now DATETIME(6);
    DECLARE v_tag_name VARCHAR(255);
    DECLARE v_thread_id BINARY(16);
    DECLARE v_loop_open INT DEFAULT 0;
    DECLARE v_loop_closed INT DEFAULT 0;
    DECLARE v_done INT DEFAULT 0;

    SET v_now = NOW(6);

    -- 1. p_tag 테이블에서 tag_name만 임시 테이블에 복사 (기반 데이터 확정)
    DROP TEMPORARY TABLE IF EXISTS tmp_tags_for_thread;
    CREATE TEMPORARY TABLE tmp_tags_for_thread
    (
        tag_name VARCHAR(255) NOT NULL PRIMARY KEY
    );

    INSERT INTO tmp_tags_for_thread (tag_name)
    SELECT tag_name
    FROM newpick4u.p_tag;

    -- 2. OPEN 상태 thread 2개 생성
    WHILE v_loop_open < 2
        DO
            -- 임의의 tag 하나 선택
            SELECT tag_name
            INTO v_tag_name
            FROM tmp_tags_for_thread
            ORDER BY RAND()
            LIMIT 1;

            -- UUID 생성
            SET v_thread_id = UUID_TO_BIN(UUID());

            -- thread 삽입
            INSERT INTO newpick4u.p_thread (thread_id,
                                            summary,
                                            tag_name,
                                            status,
                                            created_at)
            VALUES (v_thread_id,
                    CONCAT('Thread for ', v_tag_name),
                    v_tag_name,
                    'OPEN',
                    v_now);

            -- 사용한 태그 삭제 (중복 방지)
            DELETE FROM tmp_tags_for_thread WHERE tag_name = v_tag_name;

            SET v_loop_open = v_loop_open + 1;
        END WHILE;

    -- 3. CLOSED 상태 thread 5개 생성
    WHILE v_loop_closed < 5
        DO
            SELECT tag_name
            INTO v_tag_name
            FROM tmp_tags_for_thread
            ORDER BY RAND()
            LIMIT 1;

            SET v_thread_id = UUID_TO_BIN(UUID());

            INSERT INTO newpick4u.p_thread (thread_id,
                                            summary,
                                            tag_name,
                                            status,
                                            created_at)
            VALUES (v_thread_id,
                    CONCAT('Thread for ', v_tag_name),
                    v_tag_name,
                    'CLOSED',
                    v_now);

            DELETE FROM tmp_tags_for_thread WHERE tag_name = v_tag_name;

            SET v_loop_closed = v_loop_closed + 1;
        END WHILE;

    -- 사용 후 임시 테이블 삭제
    DROP TEMPORARY TABLE IF EXISTS tmp_tags_for_thread;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_threads_from_tags();