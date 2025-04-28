import http from 'k6/http';
import { check } from 'k6';

const userIds = Array.from({ length: 100 }, (_, i) => i + 1);

const newsIds = [
    'e8452ba1-df6a-464b-b397-53be92ed8609',
    '209d15d7-536a-4c0d-a6a8-1c76a14abb44',
    '6f1d51f2-6ff0-44a8-a893-3f75cef8ab24',
    'ad4f6db2-991b-4bc6-8371-d76df0dfecf6',
    '8d8b49ef-f9e9-430f-9545-8409fa2f970e',
    'a86fbb86-83d4-4de2-ad88-26e0cc17a93c',
    '8748d7ac-f7c4-4521-840e-1ba48c4ea2f7',
    '14b48a2c-7184-4acd-9550-a0ff3e9bd957',
    '60047582-0705-40ca-ac67-1c3abae7f881',
    '207434d1-6216-4938-9518-8794288e8166',
    '3c43238f-9179-4779-ae82-cdadcad0b0f9',
    '7bfce4c3-d0ab-478b-807b-084134dea1bb',
    '414e59e2-cbb7-48ce-8bf4-2dee88681dbb',
    '13d54ed2-e811-42fd-ae6d-ee82818f6618',
    '7bc80cc9-84d6-4d78-ac67-3d3cd7510dc4',
    'c43db399-a03c-418d-99b2-aca4e6c00e95',
    '4a65d62b-c501-4003-aefb-af60b223f2a1',
    'dcbc2836-7703-4a9c-8919-5dcca628b9f4',
    '1d44c2e4-bfd7-489d-bad6-2dc23bfb0764',
    '2804c56e-c2cf-4452-a78c-11b22838e49f',
    '61e2e9cf-7a08-4186-a227-d1f05c50b7a1',
    'a5f94ea3-cdf8-43de-8946-e4f9d4a2ef4b',
    '06defd74-71e5-4a6c-b168-8913cd6c03ac',
    'eb9755dd-7e03-48a6-a106-d66c271d909c',
    'f1747d72-fff6-4808-82a9-611fd5650c8e',
    '911ac481-61c2-42cb-9d6f-7f6f0e2fb59a',
    '3a48b89c-843b-4eec-aebc-87340c2dc8df',
    '39874d4b-6fbc-401f-a231-d53d50cc1baf',
    'c85c5459-c5d2-4bf6-a04b-50f39e76922c',
    'bef75cca-900e-4135-ac80-5b9740ed5c8e',
    '43903cbb-e92f-4117-9f93-648f9758eeb8',
    '42ccd20d-ff26-4d75-96e1-b4a7dc92d730',
    'c0ffcdc4-6e48-44d0-9757-b5d5319289d9',
    '82d2535a-e67a-4feb-9e5e-c132ccb3be94',
    '32e175df-bd08-4a2e-8ed5-0e168b692549',
    '5affb5ed-bf48-4cf5-ab71-4fdb26f1271b',
    'c43b3723-c580-4619-867e-1962ad1a8d4d',
    '98603f4c-46cf-4e46-aa47-686cad9d2b3d',
    '4bcb0345-078f-4990-b2ac-6c05927d0a37',
    '09e71c19-6e6d-4ca6-9dac-6130d0a0e51f',
    '312ced1b-e904-49ac-843d-702dc4ec7b7f',
    '931a04f2-5108-46e6-94eb-a06e1dcc96e3',
    '77951492-8b7a-4466-8f7c-c69185292446',
    '059c8839-1c9b-4375-a7ca-598974b74dc9',
    '0641c739-6f50-4cca-b2b5-a4baeca0eaff',
    'dc9abe2b-a05e-4ed5-88fb-e8a05b045a9f',
    'e870e232-6f6d-4125-a732-1329468e3f0e',
    '05ada6a1-d033-4256-95f4-1585b0cf388b',
    '848e7cfc-f96f-41d7-9857-b5312a2e0383',
    '6c4b80c5-5ca1-4a6b-b44f-6a881550b1d2',
    '27539d12-964c-49b6-a421-5e8d6c7d16ac',
    '7f5c43ae-c34a-4935-b9f4-36117f393008',
    '3647cb86-c1ba-4d9e-beed-50301f03e1a1',
    '87e002ee-148f-40c9-8e68-d26f31087f62',
    'de289aa6-682d-4ef0-817f-c3bcbe5f2065',
    'fea9e9ed-4123-421e-8f11-b7b4e1dd9491',
    '38029d2b-2d45-4df6-949d-6654d6c39ace',
    '576587d7-c35b-4d18-a19d-95ecb6541966',
    '12c2adb4-6b43-4e1e-8a0f-0b45fda4f457',
    '2c9a5120-7e2a-4cce-ba95-237973a81d09',
    'c05a0dff-3fd0-48dc-98c7-4ee5e9d5afa5',
    '32ba8ac9-a179-4eef-8648-5d7d40839d9d',
    'a4d7c24c-75b5-435f-b2dc-08151161eb5b',
    'b5ee37e4-0bfc-45c3-a827-6477fa76b407',
    'bb8b9c93-9727-44f5-83f6-7da7f971a434',
    'be6df30f-913e-4757-92a1-d3b4c40eba46',
    '399e5f6a-3e77-4c9d-9698-712dddfe8ca1',
    'dbeb6b28-b060-46a6-836b-52a3af0062c9',
    '9d58236d-56c5-40b7-ae0a-34725fb1029a',
    '38bdcd00-8b9b-4357-ba22-37a4e48076d9',
    '53db51a9-7804-4a0d-900c-b389e4185499',
    'eb9a9f6c-dca4-44d3-bcfc-7eb4231cba31',
    'a6c78890-e569-4f97-8453-7ffca2edfc50',
    '396e06c4-d737-4619-a2ee-b01e73e05987',
    'd5a8983f-4af0-428c-a24c-9673d8e5795e',
    '38956650-dce8-447e-8a1f-44a806bd8b7c',
    '088aad56-b107-46a5-9ba2-cd2eca57177d',
    'b2b44932-185d-43f0-8dc0-3a22a3c05f63',
    'e79bb00b-ae53-4d8a-8b22-8b7f44cadb62',
    '3614dea5-f5a8-4c59-b519-9622b1f6f0c8',
    '8c9a7dc5-10ed-4ab9-b357-74db05c0c5b2',
    '1c124d90-6cad-49a5-8933-155803642288',
    '13db087c-8f59-457a-9c7b-0e284f725f9e',
    '5809381a-ddcc-49e3-9a46-9ae24efe0586',
    '755186b7-fb11-47e5-91f2-508571adc95e'
];

export const options = {
    vus: 100, // 100명
    iterations: 2000, // 총 2000번
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed: ['rate==0'],
    },
};

export default function () {
    const BASE_URL = 'http://localhost:11001';
    const userId = userIds[Math.floor(Math.random() * userIds.length)];
    const newsId = newsIds[Math.floor(Math.random() * newsIds.length)];

    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': String(userId),
        'X-USER-ROLE': 'ROLE_MASTER',
    };

    const res = http.get(`${BASE_URL}/api/v1/news/${newsId}`, { headers });

    check(res, {
        '뉴스 상세조회 성공': (r) => r.status === 200,
    });
}
