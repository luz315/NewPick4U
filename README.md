

<br>

# 👀 Welcome
### [ 뉴스와 사람, 광고주를 연결하는 AI 기반 뉴스 커뮤니티 ]

Newpick4U 는 사용자기반맞춤뉴스서비스를제공하는 플랫폼으로 
MSA 환경에서 운영하므로, 대규모 트래픽에 안정적으로 대응할 수 있습니다.
<br>
**[ 고객중심 ]**
지속적으로 최신 뉴스를 수집합니다. 사용자의 뉴스 조회 기록을 분석하여 맞춤 뉴스를 제공합니다.
<br>
**[ 핫한주제, 쓰레드 ]**
핫한 주제를 감지하여 소통의 장:쓰레드를 생성합니다. 쓰레드에서 작성된 댓글은 여론을 분석하여 제공합니다.
<br>
**[ 광고 ]**
광고를 연동하여 고객사와 유저를 연결할 수 있습니다.


<br>


## 👨‍👩‍👧‍👦 Our Team

|염금성|이동하|임소라|전진우|
|:---:|:---:|:---:|:---:|
|[@venus-y](https://github.com/venus-y)|[@leedongha1998](https://github.com/leedongha1998)|[@luz315](https://github.com/luz315)|[@hp5234](https://github.com/hp5234)|
|BE|BE|BE|BE|
|광고 서비스 <br> 고객사 서비스 <br> 유저 서비스 <br> 공통단 작업 |태그 서비스<br>쓰레드 서비스|뉴스 서비스|프로젝트 총괄 및 기술 의사결정<br>원본뉴스 서비스<br>AI 뉴스 서비스<br>댓글 서비스<br>그라파나/프로메테우스 알림 시스템|


<br><br>
## 🌐 Architecture
![이미지설계백업 (1)](https://github.com/user-attachments/assets/30095a05-a8f8-4923-b54e-c9c77a865f38)









<br>

## 📋 ERD Diagram
![NewPick4U (1)](https://github.com/user-attachments/assets/6dce20eb-108d-4fcc-8f46-3bf19e4eab8b)


<br>



## 📝 Technologies & Tools (BE) 📝

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringCloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/> <img src="https://img.shields.io/badge/Spring%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/> <img src="https://img.shields.io/badge/JSONWebToken-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white"/> <img src="https://img.shields.io/badge/QueryDSL-009688?style=for-the-badge&logo=apache&logoColor=white"/> 

<div>
    <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/> 
    <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"/> 
    <img src="https://img.shields.io/badge/Redisson-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
    <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/>
    <img src="https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka">
    <img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white"/>

</div>

<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white"/> <img src="https://img.shields.io/badge/Zipkin-000000?style=for-the-badge&logo=zipkin&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"/> <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"/> <img src="https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white"/> <img src="https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=java&logoColor=white"/>

<div>
    <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
    <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/> 
    <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/> 
    <img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white"/>
    <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/> 
    <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"/> 
</div>
<div>
    <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/> 
    <img src="https://img.shields.io/badge/google%20gemini-8E75B2?style=for-the-badge&logo=google%20gemini&logoColor=white"/>
    <img src="https://img.shields.io/badge/Naver%20Open%20API-03C75A?style=for-the-badge&logo=naver&logoColor=white"/>

</div>
<br><br>





## 프로젝트 기능

### 🛡  뉴스 수집 기능 
> * newsorigin service + ainews service
> * 네이버 검색 API 를 활용해 뉴스기사의 주소와 제목을 수집합니다. 
> * WebClient 를 활용해 실제 뉴스 기사의 HTML 을 획득해 뉴스 본문을 추출합니다. 
> * 추출한 뉴스 본문을 Gemini AI 를 활용해 요약 및 키워드를 추출합니다. 
> * 뉴스 정보와 키워드 정보를 각각 Kafka 메세지로 발행합니다. 
![Image](https://github.com/user-attachments/assets/354f37bd-d6fb-4634-9f21-5f10d6cff671)

### 🛡  뉴스 추천 기능 
> * 사용자 태그 로그를 기록합니다.
> * 사용자 태그 로그와 뉴스를 벡터화 합니다.
> * 코사인 유사도 계산을 통해 추천 뉴스를 추출합니다.
![image](https://github.com/user-attachments/assets/8f069d11-0890-47c2-a98d-0b444bffa1eb)
![image](https://github.com/user-attachments/assets/4af5e1b6-aab8-42ea-8d62-42b5b3ca514b)



### 🛡  댓글/좋아요 기능 
> * comment service
> * 댓글을 등록/수정/삭제/조회를 수행할 수 있습니다. 
> * 핫한 주제를 감지하기 위해 
Redis 에 기록중인 키워드 가중치 데이터를 증가, 삭제 할 수 있습니다. 
> * 광고 설정에 의한 보상 포인트 지급 요청을 Kafka 메세지로 발행합니다. 
> * Redisson 분산락을 이용해 안정적으로 좋아요 등록, 취소를 수행할 수 있습니다.
![Image](https://github.com/user-attachments/assets/101017d8-cd70-4a8a-9f96-24cce07d4e0a)

### 🛡 포인트 지급 기능
> * 광고가 달린 뉴스기사에 댓글을 달 경우 포인트를 지급합니다.
> * 포인트 지급과 취소 요청 시 발생할 수 동시성 문제를 방지하기 위해 Redisson 분산락을 
    > 적용하였습니다.
> * 회원에게 포인트 지급이 정상적으로 이루어지지 않을 경우 DLQ에서 보상트랜잭션을
    > 통해 데이터를 원복합니다.

![image](https://github.com/user-attachments/assets/e54423a8-5df3-4cd4-83bb-b3bb742979d1)





### 🛡 쓰레드 생성 기능
> * 뉴스에 댓글이 달리면 해당 뉴스에 달린 태그들의 점수가 증가하게 되고 이러한 상황이 반복적으로 증가해서 특정 임계치를 초과하면 인기 태그로 선정되어 해당 태그에 대한 쓰레드를 생성하고 쓰레드 내에서 여러 의견을 나눌 수 있습니다.
> * 스케줄러를 통해 쓰레드 내 댓글들의 여론을 Gemini Api를 통해 분석하여 보여줍니다.

![image](https://github.com/user-attachments/assets/0639d532-4d68-416c-8609-f7208fa3db73)


 <br>
<br><br>




## 적용 기술

### ◻ QueryDSL

> 커서페이징, 정렬, 검색어 등에 따른 동적 쿼리 작성을 위하여 QueryDSL 도입하여 활용했습니다.

### ◻ Redis

> 마이크로서비스 간 통신 부담을 줄이고, 데이터 접근 속도를 획기적으로 개선하고 분산락을 통해 동시성을 제어하기 위하여 도입하였습니다.

### ◻ Kafka

> 트래픽이 많이 발생하는 환경에서 안정적으로 메시지를 처리하기 위해 도입하였습니다.

### ◻ MySql

> InnoDB 스토리지 엔진 기반으로 트랜잭션 무결성, 외래키, 인덱스 성능을 보장하고, jpa 사용시 Mysql Dialect 공식 지원, Spring Cloud와 jdbc 연결 및 트랜잭션 연동이 지원되어 도입하였습니다.

### ◻ Zipkin

> 분산 추적 기능 사용 시 스프링부트와 좋은 통합성과 도입이 빠르고 운영이 간편하다는 장점이 있어 도입하였습니다.

### ◻ Micrometer

> 스프링부트 기반 시스템에서 메트릭을 수집, 전송하기 위해 사용하였고, Prometheus, InfluxDb 다양한 모니터링 시스템과 통합 가능

### ◻ Prometheus
> 서버 시스템 메트릭 등 다양한 메트릭 수집이 가능하고 그라파나와 연동이 용이하다는 점에서 도입되었습니다. 


### ◻ Grafana

> 다양한 메트릭 정보를 시각화, 메트릭 데이터에 따른 알림 서비스 구성에 용이하여 선택하였습니다.


### ◻ K6

> 테스트 시나리오를 js 코드로 작성하여 복잡한 로직도 유연하게 구성 가능하다는점, 
> go 언어 기반으로 실행 시 리소스 소비가 적고, 실행 속도가 빠르다는점, 
> jMeter 보다 코드 관리가 직관적이라고 판단하여 도입하였습니다.


### ◻ Docker
> 개발/운영 환경 일관성 확보
> 프로세스 단위로 실행하여 인프라 자원 효율성 및 경량화에 유리
> 서비스마다 필요한 라이브러리, jdk 등을 Dockerfile에 정의하여 분리 가능
> OS나 실행환경에 무관하게 독립적으로 운영 가능




<br><br>

## 🚨 Trouble Shooting

### Hibernate가 메모리에서 페이징 처리

- **문제**: Hibernate가 DB에서 처리하지 못하고 **애플리케이션 메모리에서 페이징 처리**
- **영향**: 대용량 데이터 조회 시 **OOM(Out of Memory)** 또는 성능 저하 발생

**해결 방법**  
- QueryDSL로 `limit`과 `offset` 명시적으로 설정
- `.fetchResults()` 대신 `.limit().offset()` + `.fetch()` 조합 사용
- 필요 시 native query로 최적화

### 데이터 조회 시 검색 성능 저하 이슈

- **문제**: MySQL에서 %keyword%와 같은 like 연산을 수행할 경우 테이블을
    Full Scan하여 데이터를 조회 
- **영향**: 다수의 쓰레드가 동시에 검색 요청할 경우 요청 대비 낮은 처리속도로 인해
    일부 쓰레드의 경우 커넥션 타임아웃이 발생하게 됨

**해결 방법**  
- 검색처리를 ElasticSearch로 이전
- 역색인과, n-gram 기반 검색을 통해 조회속도를 대폭 향상
- MySQL과 비교했을 때 더 많은 트래픽을 안정적으로 처리

### Gemini AI 요청한도 초과 문제 

- **문제**: Gemini AI API 요청 시 **요청 실패 케이스** 발생 
- **영향**: 수집된 뉴스가 AI 에 의한 가공 실패케이스로 처리되면서 재처리를 위해 카프카 **DLQ 에 메세지가 누적**되는 현상 확인

**해결 방법**  
- Gemini AI API 의 분당 요청 한도를 초과하는 케이스로 파악 (분당 15회 한도)
- AI 가공 로직이 주기적으로 실행되는것이 아닌 카프카 메세지 컨슘에 의해 실행된다는점이 원인으로 파악
- 카프카 메세지 퍼블리셔측 메세지 발행 속도를 조절하는 방식으로 변경 (분당 10회) 

### 배치 업데이트 시 db에 값이 insert 되지 않는 문제

- **문제**: 태그 생성의 처리 속도를 높이기 위해 배치 업데이트를 구현했으나 카프카 메세지에는 태그 id가 들어갔지만, db에는 태그들이 저장되지 않음을 확인
- **영향**: 배치 업데이트 처리 시 NativeQuery + @Modifying을 사용하고 있어 직접 쿼리를 실행한 후에는 flush가 지연될 수 있음

**해결 방법**  
- @Modifying 옵션으로 flushAutomatically = true, clearAutomatically = true을 주어 jpa 영속성 컨텍스트에 쌓인 변경사항을 자동으로 flush 해주고 캐시를 자동으로 제거해줌으로 해결
<br><br>




<br>


