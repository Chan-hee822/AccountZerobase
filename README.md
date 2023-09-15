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

## 사용법

프로젝트를 실행하고 REST API 엔드포인트에 대한 요청을 수행할 수 있습니다.

- `[POST] /account`: 계좌 생성 요청을 보내어 새로운 계좌를 생성합니다.

- `[DELETE] /account`: 계좌 삭제 요청을 보내어 계좌를 삭제합니다.

- `[GET] /account`: 사용자 ID를 기반으로 사용자의 계좌 목록을 가져옵니다.

- `[GET] /account/{id}`: 계좌 ID를 기반으로 특정 계좌의 정보를 가져옵니다.

- `[POST] /transaction/use`: 잔액 사용 요청을 보내어 계좌에서 잔액을 사용합니다.

- `[POST] /transaction/cancel`: 잔액 사용 취소 요청을 보내어 이전 잔액 사용 거래를 취소합니다.

- `[GET] /transaction/{transactionId}`: 거래 ID를 기반으로 특정 거래 정보를 가져옵니다.
