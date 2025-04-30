import http from 'k6/http';
import { check } from 'k6';
import { SharedArray } from 'k6/data';

const tokens = new SharedArray('JWT Tokens', function () {
    return open('../../k6-user-tokens.txt').split('\n').filter(Boolean);
});
// const userIds = Array.from({ length: 100 }, (_, i) => i + 1);

const newsIds = new SharedArray('News IDs', () =>
    open('../../k6-news-ids.txt').split('\n').filter(Boolean)
);

export const options = {
    vus: 100, // 100명
    iterations: 2000, // 총 2000번
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed: ['rate==0'],
    },
    // scenarios: {
    //     constant_request_rate: {
    //         executor: 'constant-arrival-rate',
    //         rate: 100, // 초당 100 요청
    //         timeUnit: '1s',
    //         duration: '2m',
    //         preAllocatedVUs: 100,
    //         maxVUs: 200,
    //     },
    // },
};

export default function () {
    const BASE_URL = 'http://localhost:8080';
    const token = tokens[Math.floor(Math.random() * tokens.length)];
    // const userId = userIds[Math.floor(Math.random() * userIds.length)];
    const newsId = newsIds[Math.floor(Math.random() * newsIds.length)];

    const headers = {
        Authorization: `Bearer ${token}`,
    };
    // const headers = {
    //     'Content-Type': 'application/json',
    //     'X-USER-ID': String(userId),
    //     'X-USER-ROLE': 'ROLE_MASTER',
    // };

    const res = http.get(`${BASE_URL}/api/v1/news/${newsId}`, { headers });

    check(res, {
        '뉴스 상세조회 성공': (r) => r.status === 200,
    });
}
