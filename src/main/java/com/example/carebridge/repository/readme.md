# Repository

## 역할
- 데이터베이스 접근 계층
- 데이터 CRUD 연산 수행

## 특징
- `@Repository` 어노테이션 사용 (선택적)
- JpaRepository 인터페이스 상속
- 쿼리 메소드 정의

## 예시
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> searchByUsername(@Param("keyword") String keyword);
}