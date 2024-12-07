# Service

## 역할
- 비즈니스 로직 구현
- 트랜잭션 관리

## 특징
- `@Service` 어노테이션 사용
- 레포지토리 계층과 상호작용
- 데이터 가공 및 비즈니스 규칙 적용

## 예시
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }
}