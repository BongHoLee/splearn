# Splearn 개발 가이드

## 아키텍처
- 헥사고날 아키텍처
- 도메인 모델 패턴


### 계층
- Domain Layer
- Application Layer
- Adapter Layer

> 외부(Actor) -> 어댑터 -> 애플리케이션 -> 도메인

```
tobyspring/splearn/
├── domain/                    # 도메인 계층 (핵심 비즈니스 로직)
│
├── application/               # 애플리케이션 계층 (유스케이스)
│   ├── provided/             # 제공하는 포트 (Primary/Driving Ports)
│   └── required/             # 필요한 포트 (Secondary/Driven Ports)
│
└── adapter/                   # 어댑터 계층 (외부 시스템 연동)
    ├── webapi/               # 웹 API 어댑터
    ├── persistence/          # 영속성 어댑터
    ├── security/             # 보안 어댑터
    └── integration/          # 외부 시스템 통합 어댑터
```