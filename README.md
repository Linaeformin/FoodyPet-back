# FoodyPet 🐾

**반려동물의 영양 요구량과 보호자의 식품 재고를 함께 고려하는 맞춤 식단 추천 서비스**

FoodyPet은 반려동물 정보를 바탕으로 영양 기준을 계산하고, 현재 보유한 식품으로 급여 가능한 식단을 추천합니다. 추천 결과의 영양 성분을 확인하고 식품 재고, 식사 일기, 간식 및 물 섭취 기록을 관리할 수 있습니다.

> 이 저장소는 FoodyPet의 **Spring Boot 백엔드** 코드입니다. 서비스 기획, UI/UX 디자인, Android 앱, 백엔드와 배포는 1인 프로젝트로 진행했습니다.

## 주요 기능

| 영역 | 내용 |
| --- | --- |
| 계정 및 반려동물 | 회원가입·로그인, JWT 인증, 반려동물 정보와 급여 일정 등록 |
| 식품 및 재고 | 식품 검색·자동완성, 보유 식품 등록·수정·삭제 |
| 맞춤 식단 | 영양 기준과 재고를 고려한 끼니별 식품 조합 추천 및 결과 저장 |
| 기록 | 식사·간식·영양제·물 섭취 기록, 식사 일기와 증상 기록 |
| 커뮤니티 | 식단과 연결한 게시글 작성 및 조회 |

## 식단 추천 흐름

1. 반려동물의 영양 기준과 급여 시간을 조회하고 끼니별 목표량을 계산합니다.
2. 반려동물 유형에 맞고 수량이 남아 있으며 유통기한이 지나지 않은 재고를 후보로 고릅니다.
3. 후보 식품의 조합과 급여량을 탐색합니다. 에너지·단백질·지방·섬유질 등 목표량과의 차이, 칼슘:인 비율, 선호도와 같은 조건을 점수에 반영합니다.
4. 끼니별 조합을 선택해 저장하고, 하루 전체의 영양 성분과 기준값을 비교할 수 있게 반환합니다.

추천 후보를 고를 때 재고와 중복 식품을 정리하고, 조합 탐색 후 급여량을 조정하는 로직은 `domain/diet/service/DietRecommendService.java`에 있습니다. 두 반려묘 사례로 영양 항목을 비교한 결과를 2026 한국인터넷정보학회 추계학술발표대회 논문에 정리했습니다.

## 기술 스택

- **Backend:** Java 17, Spring Boot 4, Spring Web MVC, Spring Security, Spring Data JPA
- **Data:** MySQL, Redis
- **인증·파일:** JWT, AWS S3
- **빌드·배포:** Gradle, GitHub Actions, AWS EC2

Redis Sorted Set을 이용한 식품명 자동완성은 `PetFoodAutocompleteRedisService`에서 처리합니다. JWT 인증과 접근 제어는 `common/config` 및 `domain/user`에 구성했습니다.

## 주요 API

| 기능 | 요청 |
| --- | --- |
| 회원가입·로그인·토큰 갱신 | `POST /api/users/signup`, `POST /api/users/login`, `POST /api/users/refresh` |
| 반려동물 등록 | `POST /api/pets` |
| 보유 식품 조회 | `GET /api/foods/stocks` |
| 식품 자동완성 | `GET /api/foods/autocomplete` |
| 식단 추천·결과 조회 | `POST /api/diets/recommend`, `GET /api/diets/recommend/{dietId}` |
| 식사 일기 작성 | `POST /api/diaries/meals` |

일부 요청은 로그인 토큰이나 `multipart/form-data`가 필요합니다. 구체적인 요청·응답 형태는 각 컨트롤러와 DTO를 참고해 주세요.

## 로컬 실행

Java 17, MySQL, Redis 및 외부 저장소 설정이 필요합니다. `application-local.properties`의 데이터베이스·Redis·JWT·S3 설정을 자신의 개발 환경에 맞게 구성한 뒤 실행합니다. **실제 키와 비밀번호는 커밋하지 마세요.**

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

배포 워크플로우는 `.github/workflows/deploy.yml`에 있으며 `release` 브랜치 푸시를 기준으로 동작합니다.

## 담당 범위

기획과 화면 디자인부터 Android 앱, REST API, 추천 알고리즘, 데이터 저장 및 서버 배포까지 직접 수행했습니다. 이 저장소에는 백엔드 구현을 중심으로 정리했습니다.
