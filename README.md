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
