-- Step 1: 사용자 1,000명 생성
DROP PROCEDURE IF EXISTS newpick4u.proc_generate_users;

DELIMITER $$

CREATE PROCEDURE newpick4u.proc_generate_users()
BEGIN
    DECLARE i INT DEFAULT 0;

    -- 1234를 bcrypt로 해싱한 고정 비밀번호
    DECLARE v_encoded_password VARCHAR(100) DEFAULT '$2a$10$1tVw3E5lPAUbt8g4BhE3feXFeJ5jX0M0WBXWTxFgRf5wEYeJVoEru';
    DECLARE v_username VARCHAR(50);

    WHILE i < 1000
        DO
            -- 기본 username 생성: "user" + 번호 (앞에 0 채움)
            SET v_username = REPLACE(CONCAT('user', LPAD(i, 4, '0')), '_', '');

            -- 특수문자 제거 (MySQL에서는 직접 제거가 복잡하기 때문에 '_'만 제거한 구조로 가정)
            -- 추가로, 필요시 REGEXP_REPLACE 사용 가능 (8.0.17 이상)
            -- 완벽한 특수문자 제거는 REGEXP_REPLACE 활용

            INSERT INTO newpick4u.p_user (username,
                                          name,
                                          password,
                                          role,
                                          point,
                                          created_at)
            VALUES (v_username, -- username
                    v_username, -- name
                    v_encoded_password,
                    'ROLE_USER',
                    0,
                    NOW(6));

            SET i = i + 1;
        END WHILE;
END $$

DELIMITER ;

CALL newpick4u.proc_generate_users();