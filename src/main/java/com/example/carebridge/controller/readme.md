# Controller

## 역할
- API 엔드포인트 정의
- 클라이언트 요청 처리 및 응답 반환

## 특징
- `@RestController` 어노테이션 사용
- 요청 매핑 (`@GetMapping`, `@PostMapping` 등)
- 서비스 계층 호출

## 예시
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}