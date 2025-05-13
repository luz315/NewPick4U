-- Step 4: 태그 및 뉴스 태그 생성

DROP PROCEDURE IF EXISTS newpick4u.proc_generate_tags_and_news_tags;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_tags_and_news_tags()
BEGIN
    DECLARE v_now DATETIME(6);
    DECLARE v_tag_name VARCHAR(255);
    DECLARE v_tag_id BINARY(16);
    DECLARE v_news_id BINARY(16);
    DECLARE v_news_tag_id BINARY(16);
    DECLARE v_tag_count INT;
    DECLARE v_loop INT;
    DECLARE v_done INT DEFAULT 0;

    -- 커서 선언
    DECLARE news_cursor CURSOR FOR SELECT news_id FROM newpick4u.p_news;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    -- 1. p_tag 생성
    SET v_now = NOW(6);

    -- 미리 준비된 태그 삽입
    INSERT IGNORE INTO newpick4u.p_tag (tag_id, tag_name, created_at)
    VALUES (UUID_TO_BIN(UUID()), 'Red', v_now),
           (UUID_TO_BIN(UUID()), 'Blue', v_now),
           (UUID_TO_BIN(UUID()), 'Yellow', v_now),
           (UUID_TO_BIN(UUID()), 'Green', v_now),
           (UUID_TO_BIN(UUID()), 'Black', v_now),
           (UUID_TO_BIN(UUID()), 'White', v_now),
           (UUID_TO_BIN(UUID()), 'Korea', v_now),
           (UUID_TO_BIN(UUID()), 'Japan', v_now),
           (UUID_TO_BIN(UUID()), 'USA', v_now),
           (UUID_TO_BIN(UUID()), 'Germany', v_now),
           (UUID_TO_BIN(UUID()), 'France', v_now),
           (UUID_TO_BIN(UUID()), 'Brazil', v_now),
           (UUID_TO_BIN(UUID()), 'Pizza', v_now),
           (UUID_TO_BIN(UUID()), 'Sushi', v_now),
           (UUID_TO_BIN(UUID()), 'Kimchi', v_now),
           (UUID_TO_BIN(UUID()), 'Hamburger', v_now),
           (UUID_TO_BIN(UUID()), 'Pasta', v_now),
           (UUID_TO_BIN(UUID()), 'Tacos', v_now),
           (UUID_TO_BIN(UUID()), 'Politics', v_now),
           (UUID_TO_BIN(UUID()), 'Sports', v_now),
           (UUID_TO_BIN(UUID()), 'Economy', v_now),
           (UUID_TO_BIN(UUID()), 'Culture', v_now),
           (UUID_TO_BIN(UUID()), 'IT', v_now),
           (UUID_TO_BIN(UUID()), 'Health', v_now),
           (UUID_TO_BIN(UUID()), 'Samsung', v_now),
           (UUID_TO_BIN(UUID()), 'Apple', v_now),
           (UUID_TO_BIN(UUID()), 'Nike', v_now),
           (UUID_TO_BIN(UUID()), 'Adidas', v_now),
           (UUID_TO_BIN(UUID()), 'Toyota', v_now),
           (UUID_TO_BIN(UUID()), 'BMW', v_now);

    -- 임시 테이블에 tag 정보 저장
    DROP TEMPORARY TABLE IF EXISTS tmp_tags;
    CREATE TEMPORARY TABLE tmp_tags
    (
        tag_id   BINARY(16),
        tag_name VARCHAR(255)
    );

    INSERT INTO tmp_tags (tag_id, tag_name)
    SELECT tag_id, tag_name
    FROM newpick4u.p_tag;

    -- 2. p_news_tag 생성
    OPEN news_cursor;

    news_loop:
    LOOP
        FETCH news_cursor INTO v_news_id;
        IF v_done = 1 THEN
            LEAVE news_loop;
        END IF;

        -- 2 ~ 10개의 랜덤 태그 선택
        SET v_tag_count = FLOOR(RAND() * 9) + 2; -- 2 ~ 10개

        SET v_loop = 0;
        WHILE v_loop < v_tag_count
            DO
                -- 랜덤 태그 선택
                SELECT tag_id, tag_name
                INTO v_tag_id, v_tag_name
                FROM tmp_tags
                ORDER BY RAND()
                LIMIT 1;

                -- p_news_tag에 삽입
                SET v_news_tag_id = UUID_TO_BIN(UUID());

                INSERT INTO newpick4u.p_news_tag (news_tag_id,
                                                  news_id,
                                                  tag_id,
                                                  name)
                VALUES (v_news_tag_id,
                        v_news_id,
                        v_tag_id,
                        v_tag_name);

                SET v_loop = v_loop + 1;
            END WHILE;
    END LOOP;

    CLOSE news_cursor;

    DROP TEMPORARY TABLE IF EXISTS tmp_tags;

END $$

DELIMITER ;

CALL newpick4u.proc_generate_tags_and_news_tags();
