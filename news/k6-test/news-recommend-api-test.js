import http from 'k6/http';
import { check } from 'k6';

const userIds = Array.from({ length: 100 }, (_, i) => i + 1); // 1번 ~ 100번 유저

export const options = {
    vus: 100, // 100명 동시 사용자
    iterations: 2000, // 1인당 20번 호출 (100 * 20 = 2000)
    thresholds: {
        http_req_duration: ['p(95)<200'], // 95% 요청이 200ms 이하
        http_req_failed: ['rate==0'], // 실패율 0%
    },
};

export default function () {
    const BASE_URL = 'http://localhost:11001'; // 실제 주소
    const userId = userIds[Math.floor(Math.random() * userIds.length)];

    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': String(userId),
        'X-USER-ROLE': 'ROLE_MASTER',
    };

    const res = http.get(`${BASE_URL}/api/v1/news/recommend`, { headers });

    check(res, {
        '추천 뉴스 조회 성공': (r) => r.status === 200,
    });
}
