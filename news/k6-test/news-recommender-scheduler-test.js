import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1, // 1명
    iterations: 1, // 한번만 호출
};

export default function () {
    const BASE_URL = 'http://localhost:11001';
    const res = http.post(`${BASE_URL}/api/v1/news/admin/scheduler/update-recommendations`);

    check(res, {
        'status was 200': (r) => r.status === 200,
    });

    sleep(1);
}
