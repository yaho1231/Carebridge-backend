# Model (Entity)

## 역할
- 데이터베이스 테이블과 매핑
- 도메인 객체 정의

## 특징
- `@Entity` 어노테이션 사용
- JPA 매핑 어노테이션 활용
- 데이터베이스 스키마 표현

## 예시
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true)
    private String email;
}