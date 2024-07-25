## 인증, 인가를 수행하는 백엔드 서버

Scale-out시 세션 불일치를 해결하는 WAS 서버를 구현한다.

### 기능사항

사용자는 회원가입을 할 수 있다.  
사용자는 로그인을 할 수 있다.  
사용자는 세션 유지를 확인할 수 있다.  
로드밸런싱시 다른 서버로 연결이 포워딩되더라도 세션이 유지된다.  
요청 성공, 실패 시 명확한 응답을 받는다.   

세션 클러스터링을 위한 실습으로 db는 고려하지 않는다.  
Spring Session은 과정 이해를 위해 사용하지 않는다.  

[API 문서](./docs/api.pdf)

### 기술적 고려사항

이 프로젝트에서는 Redis를 통해 세션을 관리한다. 이를 선택하기까지 든 의문을 정리한다.  

- 왜 HttpSession을 사용하지 않았나?

Tomcat이 지원하는 HttpSession은 편리하지만 톰캣 서버 내에서만 세션을 유지한다.    
HttpSession이 static한 Map으로 세션을 관리하고 있기 때문이다.   
즉, 여러 서버를 띄울 경우 세션 불일치가 발생한다. (B서버 프로세스는 A서버 프로세스 메모리 접근 불가.)  

- Tomcat Session Clustering도 있는데 왜 사용하지 않았나?

Tocmat에서 지원하는 Session Clustering은 세션을 공유하는 방식이 아닌 세션을 복제하는 방식이다.  
서버, 트래픽이 늘어날수록 세션의 동기화에 많은 네트워크 비용이 든다.   

- Sticky Session도 있지 않나?

클라이언트 요청을 항상 고정된 서버로만 보내는 방식. 한 서버로 집중되어 부하가 발생할 수 있다.  
다른 서버로 요청을 보낼 수가 없기 때문에 서버를 scale-out한 의미가 불분명해진다.

이러한 이유들로 인하여 여러 서버가 공통된 세션 저장소를 바라보게 하고 여러 개의 WAS는 무상태로 만들어 확장이 용이하게 구성했다.  

---

- 하나의 공통된 저장소면 DB 테이블을 하나 더 늘리거나 세션용 RDBMS를 두면 되는 거 아닌가?

세션 확립과 세션 확인을 통한 인가는 웹 서비스의 대부분의 요청에 필요하고 대개 DB접근 로직을 수행하기 전에 선행된다.    
디스크 기반으로 동작하는 DBMS를 사용할 경우 I/O시간이 많이 소요되고 DB접근이 필요없던 요청(캐시로 해결할 수 있는 요청 등)도 DB커넥션 풀을 점령하게 된다.  
따라서 인메모리에서 실행되는 DB에 세션을 저장하여 보다 빠른 성능을 기대할 수 있다.  

- 인메모리 db로 왜 Redis? Memcached도 있는데?

Redis는 다양하게 활용될 수 있다. 다양한 데이터 구조를 지원하고 있어 세션 정보를 원하는 대로 커스텀할 수 있다.  
영속화 옵션을 지원하기 때문에 서버가 다운되어도 세션 정보가 유지될 수 있게 운영할 수도 있다.  
또한 cluster 모드를 지원하여 웹서비스의 트래픽에 맞게 확장된 구성을 가질 수도 있다.  

- Redis를 세션 저장소로 사용했을 때 단점은?

인메모리 DB이기 때문에 서버가 다운되면 세션 정보가 모두 사라진다.  
인메모리 DB이기 때문에 디스크를 사용하는 DB보다 저장 용량이 적어 대량의 정보가 입력될 시 메모리 부족 현상이 발생할 수 있다.  
현 프로젝트에서 Stand Alone으로 구성되어 있기 때문에 트래픽 증가시 Redis가 병목이 될 수 있다.  

- 이를 해결하는 방법은?  

Redis를 다중화한다. Redis Cluster 혹은 Redis Sentinel을 구성하여 트래픽이 많아져도 Redis가 병목이 되지 않도록 한다.   
세션 정보의 영속화가 필요하다면 Redis가 지원하는 RDB, AOF를 적절히 구성하여 Redis가 다운되어도 세션 정보를 복구할 수 있도록 한다.  
Redis의 다중화와 영속화도 주의해야할 점들이 있다.  
다중화된 Redis 서버들은 데이터의 일관성을 유지하는 것에 문제가 생길 수 있고, 영속화는 성능 저하를 가져올 수 있다.  

따라서 적절한 모니터링과 관리가 필요하겠다. 

---

- Bcrypt를 PasswordEncoder로 쓴 이유는?

SHA, MD5 등의 해시함수는 레인보우 테이블 공격(많이 쓰는 패스워드 테이블에 암호 함수를 쓰고 대조하는 방법), 
브루트 포스 공격(완전 탐색으로 암호 함수 연산을 돌림. 패스워드가 대개 짧지 않고 많이 쓰는 건 정해져 있으므로 연산이 빠를수록 빨리 뚫림)에 취약하다. 
동일한 메세지에 대해 동일한 해시 결과값을 가지고 암호화를 위한 알고리즘이 아니기 때문에 속도가 빠르기 때문.

Bcrypt는 솔트, 키스트레칭이 적용된 검증된 암호화 시스템이다.
동일한 메세지에 솔트를 추가하여 다른 결과값을 가지게 하여 123456에도 다른 해시 결과를 가지게 하므로 레인보우 테이블 공격을 어렵게 한다.
해시함수를 여러번 적용하여 연산에 걸리는 시간을 의도적으로 늘려 브루트 포스 공격을 어렵게 한다.

---

- 테스트, 배포 자동화에 Github Actions를 사용한 이유

Github Actions를 사용해 본 경험이 있어서 익숙하다.
Jenkins는 레퍼런스가 많고 다양한 플러그인을 지원하지만 그만큼 설정이 어렵다.
무엇보다 별개의 서버가 따로 필요한데 Jenkins만을 위한 서버를 띄우기 돈, 시간이 부담스럽다.

- 현재 인프라 환경의 문제점은?

단일 서버(Naver Cloud Platform 무료 Micro 서버)내에 Redis, WAS, Nginx, DB가 모두 설치되어 있어 서버가 다운되면 모든 서비스가 중단된다.
세션 클러스터링을 하는 이유는 서버의 scale-out을 대비하기 위함인데 현재 상황에서는 scale-out이 불가능하다.
프로젝트의 의도대로 무상태인 WAS계층을 만들려면 Nginx, Redis, DB 서버를 따로 두고 WAS를 여러대 띄울 수 있어야 한다.

- nginx를 사용한 이유는?

웹 서비스의 표준포트는 80, 443이다.
WAS를 80 or 443으로 사용하면 이후 서버가 확장될 때 충돌이 발생할 수 있다.
WAS를 비표준포트로 두고 Nginx를 활용하여 무중단 배포, 로드밸런싱, HTTPS 적용을 할 수 있는 확장성을 열었다.

- 웹서버면 apache도 있는데 왜 nginx를 사용했나?

apache는 요청 기반 멀티프로세스 + 멀티스레드 모델을 사용한다.
쓰레드풀을 운영하고 요청이 많아지면 프로세스를 fork한다. 이는 메모리 사용량을 예측할 수 없게 한다. 작은 서버에서는 치명적이다.
nginx는 멀티프로세스지만 요청당 스레드를 할당하는 방식이 아니다. 
Master process가 여러개의 Worker Process를 관리하며 Worker Process는 싱글 스레드, 이벤트 기반으로 통신을 비동기 처리한다.
따라서 메모리 사용량이 적고, 예측 가능하여 작은 서버에 제격이라 사용했다.

### how to run

Git Clone this repository

Suppose you have docker-compose installed.

```
docker-compose up -d
```

### how to test

docker-compose create two server instances whose ports are 8080, 8081.   
You can test the server by sending request to 8080, 8081.

### Project Structure

```
├── SessionClusterApplication.java
├── global
│   ├── auth
│   │   ├── AuthEmail.java
│   │   ├── AuthException.java
│   │   ├── AuthInterceptor.java
│   │   ├── AuthMember.java
│   │   ├── AuthMemberArgumentResolver.java
│   │   ├── AuthResponse.java
│   │   ├── password
│   │   │   ├── BcryptPasswordEncoder.java
│   │   │   └── PasswordEncoder.java
│   │   └── session
│   │       ├── RedisSessionManager.java
│   │       └── SessionManager.java
│   ├── config
│   │   ├── RedisConfig.java
│   │   └── WebMvcConfig.java
│   └── exception
│       ├── ErrorResponse.java
│       └── GlobalExceptionHandler.java
└── member
    ├── application
    │   ├── MemberLoginService.java
    │   └── MemberService.java
    ├── dao
    │   ├── JdbcTemplateMemberDAO.java
    │   └── MemberDAO.java
    ├── domain
    │   └── Member.java
    ├── exception
    │   └── MemberException.java
    └── ui
        ├── MemberRestController.java
        └── dto
            ├── request
            │   ├── MemberCreateRequest.java
            │   └── MemberLoginRequest.java
            └── response
                └── MemberResponse.java

```
