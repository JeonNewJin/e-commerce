import http from 'k6/http';
import {check, sleep} from 'k6';

export let options = {
    vus: 100,            // 동시 사용자 수
    duration: '30s',     // 테스트 지속 시간
};

export default function () {
    const res = http.get(`http://localhost:8080/api/v1/products?page=0&size=20&sort=LATEST`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response is not empty': (r) => r.body && r.body.length > 0,
    });

    // 사용자 대기 시간
    sleep(1);
}