-- Step 6: 고객사, 광고 정보 생성
DROP PROCEDURE IF EXISTS newpick4u.proc_generate_clients_and_advertisements;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_clients_and_advertisements()
BEGIN
    -- 변수 선언
    DECLARE v_now DATETIME(6);
    DECLARE v_client_id BINARY(16);
    DECLARE v_advertisement_id BINARY(16);
    DECLARE v_news_id BINARY(16);
    DECLARE v_client_count INT DEFAULT 0;
    DECLARE v_industry VARCHAR(50);
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_news_count INT DEFAULT 0;

    -- 커서 선언
    DECLARE news_cursor CURSOR FOR
        SELECT news_id
        FROM newpick4u.p_news
        ORDER BY RAND()
        LIMIT 30;

    -- 핸들러 선언
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    -- p_news 데이터 수량 검증
    SELECT COUNT(*) INTO v_news_count FROM newpick4u.p_news;
    IF v_news_count < 5 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Not enough news data. Need at least 30 news records.';
    END IF;

    -- 현재 시간 설정
    SET v_now = NOW(6);

    -- 임시 테이블 생성 (client 정보를 저장)
    DROP TEMPORARY TABLE IF EXISTS tmp_clients;
    CREATE TEMPORARY TABLE tmp_clients
    (
        client_id BINARY(16)  NOT NULL,
        industry  VARCHAR(50) NOT NULL,
        PRIMARY KEY (client_id)
    );

    -- 클라이언트 생성
    WHILE v_client_count < 14
        DO
            CASE (v_client_count MOD 7)
                WHEN 0 THEN SET v_industry = 'EDUCATION';
                WHEN 1 THEN SET v_industry = 'FINANCE';
                WHEN 2 THEN SET v_industry = 'HEALTHCARE';
                WHEN 3 THEN SET v_industry = 'MANUFACTURING';
                WHEN 4 THEN SET v_industry = 'REAL_ESTATE';
                WHEN 5 THEN SET v_industry = 'TECHNOLOGY';
                WHEN 6 THEN SET v_industry = 'TRANSPORTATION';
                END CASE;

            SET v_client_id = UUID_TO_BIN(UUID());

            INSERT INTO newpick4u.p_client (client_id,
                                            email,
                                            name,
                                            phone,
                                            address,
                                            industry,
                                            created_at)
            VALUES (v_client_id,
                    CONCAT('client', LPAD(v_client_count, 4, '0'), '@example.com'),
                    CONCAT('Client ', v_client_count),
                    CONCAT('010-', LPAD(FLOOR(RAND() * 10000), 4, '0'), '-',
                           LPAD(FLOOR(RAND() * 10000), 4, '0')),
                    CONCAT('Address ', v_client_count),
                    v_industry,
                    v_now);

            INSERT INTO tmp_clients (client_id, industry)
            VALUES (v_client_id, v_industry);

            SET v_client_count = v_client_count + 1;
        END WHILE;

    -- 광고 생성
    OPEN news_cursor;

    news_loop:
    LOOP
        FETCH news_cursor INTO v_news_id;
        IF v_done = 1 THEN
            LEAVE news_loop;
        END IF;

        SELECT client_id
        INTO v_client_id
        FROM tmp_clients
        ORDER BY RAND()
        LIMIT 1;

        SET v_advertisement_id = UUID_TO_BIN(UUID());

        INSERT INTO newpick4u.p_advertisement (advertisement_id,
                                               client_id,
                                               news_id,
                                               title,
                                               content,
                                               url,
                                               is_point_grant_finished,
                                               max_point_grant_count,
                                               point,
                                               point_grant_count,
                                               budget,
                                               type,
                                               created_at)
        VALUES (v_advertisement_id,
                v_client_id,
                v_news_id,
                CONCAT('Ad for News ', HEX(v_news_id)),
                'This is an advertisement content.',
                'https://example.com',
                0,
                5,
                1000,
                0,
                5000,
                'NATIVE',
                v_now);
    END LOOP;

    CLOSE news_cursor;

    DROP TEMPORARY TABLE IF EXISTS tmp_clients;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_clients_and_advertisements();
