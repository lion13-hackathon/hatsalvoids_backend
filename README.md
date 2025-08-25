# HE ALWAYS 거리 (HatsalVoids)

> **실시간 그늘 레이어로 여름 보행을 시원하게 만들고, 동선 맥락의 장소를 자연스럽게 연결하는 AI 지도 서비스**

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [API 문서](#api-문서)
- [설치 및 실행](#설치-및-실행)
- [개발 가이드](#개발-가이드)
- [기획 및 비전](#기획-및-비전)

## 🎯 프로젝트 개요

'HE ALWAYS 거리'는 여름철 도심 보행의 불쾌감과 건강 위험을 줄이는 것을 목표로 한 AI 기반 보행 지도 서비스입니다.

### 핵심 가치 제안

- **"길찾기"가 아닌 "지도"**: 시공간적 환경 정보를 시각화하여 사용자의 판단과 경험을 돕습니다
- **실시간 그늘 계산**: 태양 고도, 건물 높이, 일사량을 바탕으로 특정 시점·위치에서의 실제 그늘 분포를 계산
- **맥락 기반 장소 큐레이션**: 웹소켓을 통한 실시간 주변 상권 정보 제공

### 타겟 사용자

- 여름철 보행에서 쾌적함을 원하는 시민·관광객
- 그늘 동선에 맞춰 노출되는 지역 상권(카페·식당·점포)

## ✨ 주요 기능

### 1. 실시간 그늘 시각화

- **위치 기반 그늘 계산**: 사용자 위치 기준 반경 내 건물들의 그늘을 실시간 계산
- **시간대별 정확도**: 태양 고도와 방위각을 고려한 정확한 그늘 위치 제공
- **건물 높이 반영**: VWorld API를 통한 실제 건축물 높이 데이터 활용

### 2. 주변 장소 정보

- **카테고리별 검색**: 카페, 음식점, 약국 등 카테고리별 주변 장소 검색
- **AI 기반 요약**: 네이버 블로그 크롤링과 OpenAI를 활용한 장소별 맞춤형 요약 제공
- **실시간 웹소켓 통신**: 사용자 위치 변경 시 실시간으로 장소 정보 업데이트

### 3. 지도 인터페이스

- **OpenLayers 기반**: 웹 기반 지도 렌더링
- **VWorld 타일 서비스**: 정확한 지도 데이터 제공
- **10초 주기 위치 갱신**: 실시간 사용자 위치 추적

## 🛠 기술 스택

### Backend

- **Java 17** - 메인 개발 언어
- **Spring Boot 3.5.4** - 웹 애플리케이션 프레임워크
- **Spring WebSocket** - 실시간 통신
- **Gradle** - 빌드 도구

### 외부 API 및 라이브러리

- **VWorld API** - 건물 정보 및 GIS 데이터
- **Kakao Map API** - 장소 검색
- **Naver Blog API** - 블로그 검색 및 크롤링
- **OpenAI API** - AI 기반 텍스트 요약
- **Proj4j** - 좌표계 변환
- **JTS (Java Topology Suite)** - 기하학적 계산
- **Solar Positioning** - 태양 위치 계산

### Frontend (예정)

- **OpenLayers** - 지도 렌더링
- **WebSocket** - 실시간 통신

## 📁 프로젝트 구조

```
src/main/java/com/example/hatsalvoids/
├── building/                    # 건물 관련 서비스
│   ├── BuildingService.java    # 건물 정보 및 요약 서비스
│   └── model/                  # 건물 관련 모델
├── external/                   # 외부 API 연동
│   ├── ExternalApiCaller.java  # 공통 API 호출 클래스
│   ├── kakao/                  # 카카오 맵 API
│   ├── naver/                  # 네이버 API
│   ├── openai/                 # OpenAI API
│   └── vworld/                 # VWorld API
├── global/                     # 전역 설정 및 유틸리티
│   ├── config/                 # 설정 클래스들
│   ├── error/                  # 에러 처리
│   ├── success/                # 성공 응답
│   ├── utils/                  # 유틸리티 클래스들
│   └── websocket/              # 웹소켓 핸들러
├── shade/                      # 그늘 계산 서비스
│   ├── ShadeController.java    # 그늘 API 컨트롤러
│   ├── ShadeService.java       # 그늘 계산 로직
│   └── model/                  # 그늘 관련 모델
└── HatsalvoidsApplication.java # 메인 애플리케이션
```

## 🔌 API 문서

### 그늘 정보 조회

```
GET /api/v1/shades
```

**파라미터:**

- `latitude` (String): 위도
- `longitude` (String): 경도
- `radius` (double): 반경 (미터)
- `time` (String): 시간 (ISO 8601 형식)
- `zoneId` (String): 시간대 ID (예: "Asia/Seoul")

**응답 예시:**

```json
{
  "status": 200,
  "message": "그늘 정보 조회 성공",
  "data": [
    {
      "buildingName": "건물명",
      "buildingHeight": 50.0,
      "geometry": {
        "building": [...],
        "shade": [...]
      }
    }
  ]
}
```

### 웹소켓 연결

```
WebSocket: /ws/echo
```

**요청 메시지:**

```json
{
  "type": "buildingSpec",
  "requestId": "unique-id",
  "payload": {
    "x": "127.123456",
    "y": "37.123456",
    "radius": "500"
  }
}
```

## 🚀 설치 및 실행

### 사전 요구사항

- Java 17 이상
- Gradle 7.0 이상

### 1. 프로젝트 클론

```bash
git clone [repository-url]
cd hatsalvoids
```

### 2. 환경 설정

`src/main/resources/application.yml` 파일에 필요한 API 키들을 설정하세요:

```yaml
spring:
  profiles:
    active: dev

# 외부 API 키 설정 (실제 환경에서는 환경변수 사용 권장)
vworld:
  api-key: your-vworld-api-key

kakao:
  api-key: your-kakao-api-key

naver:
  client-id: your-naver-client-id
  client-secret: your-naver-client-secret

openai:
  api-key: your-openai-api-key
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

## 👨‍💻 개발 가이드

### 개발 환경 설정

1. IDE에서 Java 17 설정
2. Gradle 프로젝트 임포트
3. 필요한 API 키 설정

### 주요 개발 포인트

#### 그늘 계산 로직

- `ShadeService.java`에서 태양 위치 계산 및 그늘 기하학 처리
- EPSG:5186 좌표계 사용으로 정확한 거리 계산
- 건물 높이와 태양 고도/방위각을 고려한 그림자 벡터 계산

#### 웹소켓 실시간 통신

- `EchoWebSocketHandler.java`에서 클라이언트와 실시간 통신
- 비동기 처리로 장소 정보 요약 생성
- 카테고리별 장소 검색 및 AI 요약 파이프라인

#### 외부 API 연동

- `ExternalApiCaller.java`에서 공통 API 호출 로직
- 에러 처리 및 재시도 로직 포함
- VWorld, Kakao, Naver, OpenAI API 통합

### 테스트 실행

```bash
./gradlew test
```

## 🎯 기획 및 비전

### 서비스 비전

도심 보행의 체감 환경을 데이터로 증강하는 표준 지도 레이어로 자리잡아, 시민 건강·안전·상권 활력을 동시에 높이는 플랫폼

### 핵심 차별점

- **체감 환경 레이어**: 기존 지도 서비스와 달리 실제 체감 환경 정보 제공
- **사용자 선택 중심**: 경로 제시가 아닌 정보 제공으로 사용자 판단 지원
- **상권 연계**: 자연스러운 동선 기반의 장소 큐레이션

### KPI 목표

- D7 보행 세션당 평균 체류시간 (+15%)
- 그늘 경로 이용률
- 주변 장소 카드 클릭률/전환율
- 재방문율
- 상권 측면 일 매출 변화 지표 (파일럿)

### 로드맵

- **M0-M1**: MVP 고도화 (그늘 정확도·UI 안정화)
- **M2-M3**: 파일럿 (1~2개 상권), 데이터 리포트 초안
- **M4-M6**: 지자체 협력 PoC, 접근성/이동약자 모드 확장
- **이후**: B2B/B2G 상용화, 계절 레이어(바람, 미세먼지 등) 확장

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**HE ALWAYS 거리** - 여름철 도심 보행을 시원하게 만드는 AI 지도 서비스
