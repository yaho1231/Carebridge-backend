package com.example.carebridge.service;

import com.example.carebridge.dto.KakaoDto;
import com.example.carebridge.dto.UserAccountDto;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.repository.UserAccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class OAuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect.uri}")
    private String KAKAO_REDIRECT_URL;

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";


    public OAuthService(UserAccountRepository userAccountRepository, UserAccountService userAccountService) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
    }

    public String getKakaoLogin() {
        System.out.println();
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&response_type=code";
    }

    public String getKakaoToken(String code) throws Exception {
        if (code == null) throw new Exception("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("code"         , code);
            params.add("redirect_uri" , KAKAO_REDIRECT_URL);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken  = (String) jsonObj.get("access_token");
            refreshToken = (String) jsonObj.get("refresh_token");
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        System.out.println(accessToken);
//        return getUserInfoWithToken(accessToken);
        return accessToken;
    }

    public KakaoDto getUserInfoWithToken(String accessToken) throws Exception {
        //HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        //Response 데이터 파싱
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject account = (JSONObject) jsonObj.get("kakao_account");
        JSONObject profile = (JSONObject) account.get("profile");

        long id = (long) jsonObj.get("id");
        String email = String.valueOf(account.get("email"));
        String nickname = String.valueOf(profile.get("nickname"));

        return KakaoDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname).build();
    }

    public void unlinkKakaoAccount(String accessToken) throws Exception {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken); // 액세스 토큰 추가
            headers.add("Content-type", "application/x-www-form-urlencoded");

            // RestTemplate 사용
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            // 카카오 API 호출 (unlink)
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/unlink", // 카카오 Unlink API URL
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

//            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            throw new Exception("Failed to unlink Kakao account", e);
        }
    }

    public UserAccountDto ifNeedKakaoInfo (KakaoDto kakaoDto) {
        // DB에 중복되는 email이 있는지 확인
        String kakaoEmail = kakaoDto.getEmail();
        UserAccount kakaoMember = userAccountRepository.findByEmail(kakaoEmail)
                .orElse(null);

        // DB에 정보가 없다면 회원가입 진행
        if (kakaoMember == null) {
            String kakaoNickname = kakaoDto.getNickname();

            //kakao에서 가져올 수 있는 데이터가 한정되어 모두 임의로 값을 추가함.
            String kakaoPhoneNum = "010"+userAccountService.generateRandomNumber(8);
            UserAccount.Gender kakaoGender = Math.random() < 0.5 ? UserAccount.Gender.Male : UserAccount.Gender.Female;
            LocalDateTime kakaoBirthday = LocalDateTime.now();

            //kakao 를 통해 가져온 정보
            UserAccount registerMember = new UserAccount();
            registerMember.setEmail(kakaoEmail);
            registerMember.setName(kakaoNickname);

            //임시로 추가한 데이터
            registerMember.setGender(kakaoGender);
            registerMember.setBirthDate(kakaoBirthday);
            registerMember.setPhoneNumber(kakaoPhoneNum);
            userAccountRepository.save(registerMember);

            // DB 재조회
            kakaoMember = userAccountRepository.findByEmail(kakaoEmail)
//                    .orElseThrow(() -> new NoSuchElementException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
                    .orElseThrow(() -> {
                        return new NoSuchElementException("메세지가 존재하지 않습니다.");
                    });
        }

        return userAccountService.convertUserAccountToUserAccountDto(kakaoMember);
    }

    public void kakaoDisconnect(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/logout",
                HttpMethod.POST,
                kakaoLogoutRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        System.out.println("반환된 id: "+id);
    }


}

