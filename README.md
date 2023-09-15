# AccountZeroBase

AccountZeroBase는 Java와 Spring Framework를 사용하여 개발된 계좌 관리 시스템입니다. 이 프로젝트는 여러 개의 컴포넌트로 구성되어 있으며, 각 컴포넌트는 다음과 같습니다.

- `JapAuditingConfiguration`: JPA auditing을 활성화하는 Spring Configuration 클래스입니다.

- `LocalRedisConfig`: 로컬 개발 환경에서 사용하는 Redis 설정을 관리하는 Spring Configuration 클래스입니다.

- `RedisRepositoryConfig`: Redis에 접근하기 위한 Redisson 클라이언트 설정을 관리하는 Spring Configuration 클래스입니다.

- `AccountController`: 계좌 관련 API 엔드포인트를 처리하는 Spring REST 컨트롤러입니다.

- `TransactionController`: 거래 관련 API 엔드포인트를 처리하는 Spring REST 컨트롤러입니다.

- `AccountService`: 계좌 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.

- `LockAopAspect`: AOP (Aspect-Oriented Programming)를 사용하여 계좌 잠금 및 잠금 해제를 관리하는 Aspect 클래스입니다.

- `LockService`: Redis를 사용하여 계좌 잠금을 관리하는 서비스 클래스입니다.

- `TransactionService`: 거래 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.

## 설치 및 설정

1. Git 저장소를 클론합니다.

```bash
git clone https://github.com/your-username/AccountZeroBase.git
