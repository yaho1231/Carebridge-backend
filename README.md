# nursing-care-service-app-back-end
간호간병통합서비스 앱 백엔드 

## Firebase Cloud Messaging (FCM) 설정 방법

프로젝트 실행을 위해서는 Firebase Cloud Messaging 설정이 필요합니다.

### FCM 설정 단계

1. Firebase Console 접속
   - [Firebase Console](https://console.firebase.google.com/)에 접속
   - 프로젝트 선택 또는 새 프로젝트 생성

2. 서비스 계정 키 발급
   - 프로젝트 설정 > 서비스 계정 탭으로 이동
   - "새 비공개 키 생성" 버튼 클릭
   - JSON 형식의 키 파일 다운로드

3. 환경 변수 설정
   다운로드 받은 JSON 파일의 내용을 기반으로 다음 환경 변수들을 설정합니다:

   ```bash
   export FIREBASE_PROJECT_ID="your-project-id"
   export FIREBASE_PRIVATE_KEY_ID="your-private-key-id"
   export FIREBASE_PRIVATE_KEY="your-private-key"
   export FIREBASE_CLIENT_EMAIL="your-client-email"
   export FIREBASE_CLIENT_ID="your-client-id"
   export FIREBASE_CLIENT_CERT_URL="your-client-cert-url"
   ```

   Windows 환경의 경우:
   ```cmd
   set FIREBASE_PROJECT_ID=your-project-id
   set FIREBASE_PRIVATE_KEY_ID=your-private-key-id
   set FIREBASE_PRIVATE_KEY=your-private-key
   set FIREBASE_CLIENT_EMAIL=your-client-email
   set FIREBASE_CLIENT_ID=your-client-id
   set FIREBASE_CLIENT_CERT_URL=your-client-cert-url
   ```

### 개발 환경 설정

1. IDE에서 실행하는 경우
   - IDE의 환경 변수 설정에 위의 변수들을 추가
   - IntelliJ IDEA: Run/Debug Configurations > Environment variables

2. 서버 환경에서 실행하는 경우
   - 시스템 환경 변수로 설정
   - 또는 배포 스크립트에 포함

### 주의사항
- 환경 변수 값은 절대로 버전 관리 시스템에 포함하지 마세요
- 개발/스테이징/프로덕션 환경별로 다른 키를 사용하는 것을 권장합니다
- private key에 포함된 개행문자(\n)를 올바르게 처리해야 합니다

### 문제 해결
- FCM 관련 오류가 발생하면 다음을 확인하세요:
  1. 모든 필요한 환경 변수가 설정되어 있는지
  2. 환경 변수 값이 올바른 형식인지
  3. Firebase 프로젝트 설정이 올바른지
