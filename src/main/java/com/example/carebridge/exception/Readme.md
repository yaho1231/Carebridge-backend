# Exception

## 역할
- 애플리케이션에서 발생하는 예외를 정의하고 처리
- 비즈니스 로직에 특화된 커스텀 예외 클래스 제공

## 특징
- 일반적으로 `RuntimeException`을 상속받아 구현
- 예외 발생 시 상세 정보를 포함할 수 있는 구조 제공
- `@ControllerAdvice`와 함께 사용하여 전역 예외 처리 가능

## 예시
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class BusinessLogicException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}