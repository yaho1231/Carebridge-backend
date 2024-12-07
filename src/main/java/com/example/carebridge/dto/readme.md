# DTO (Data Transfer Object)

## 역할
- 계층 간 데이터 전송을 위한 객체
- API 요청/응답 데이터 구조 정의

## 특징
- 단순한 데이터 컨테이너
- 비즈니스 로직 없음
- 엔티티와 분리된 데이터 표현

## 예시
```java
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    // 생성자, getter, setter
}