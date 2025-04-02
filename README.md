# <p align="center">🏥 CareBridge</p>
<p align="center"><i>간호간병통합서비스 플랫폼</i></p>

<div align="center">
  <img src="./logo.jpg" alt="CareBridge 로고" width="250">
  <h3>병원 입원 생활을 더 스마트하게, 더 편리하게</h3>
  
  [![SpringBoot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
  [![MySQL](https://img.shields.io/badge/MySQL-blue.svg)](https://www.mysql.com/)
  [![Swagger](https://img.shields.io/badge/Swagger-API_Docs-85EA2D.svg)](https://carebridge.kro.kr/swagger-ui/index.html)
</div>

<hr>

## 📋 프로젝트 소개

> **CareBridge**는 입원 환자들의 병원 생활을 보다 편안하고 효율적으로 지원하는 **간호간병 통합 컨시어지 지원 플랫폼**입니다. 환자와 의료진 간의 원활한 의사소통을 도모하고, 병원의 의료 서비스 효율성을 극대화하며, 환자 만족도를 향상시키는 것을 목표로 합니다.

<br>

## 🎯 핵심 기능

<table style="width: 100%; border-collapse: separate; border-spacing: 0 10px;">
  <tr>
    <td width="50%" style="padding: 15px; vertical-align: top;">
      <h3>📱 스마트 콜벨 서비스</h3>
      <ul>
        <li>환자와 간병인이 메시지 기반으로 필요한 서비스(식사, 청소, 간호 등)를 요청하면 AI가 자동 분류</li>
        <li>요청 유형에 따라 적절한 처리 경로로 자동 분배:
          <ul>
            <li>콜벨 요청: 담당 의료진에게 즉시 전달</li>
            <li>일반 문의: 의료진과 실시간 소통</li>
            <li>의료 상담: ChatGPT LLM 기반 프롬프트를 통해 정보 자동 응답</li>
          </ul>
        </li>
      </ul>
    </td>
    <td width="50%" style="padding: 15px; vertical-align: top;">
      <h3>🏥 입원 생활 편의성 향상</h3>
      <ul>
        <li>입퇴원 절차, 병실 생활 정보, 의료 행정 서비스 등의 정보를 쉽게 확인</li>
        <li>환자 맞춤형 정보와 서비스를 원스톱으로 제공하는 통합 플랫폼</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td style="padding: 15px; vertical-align: top;">
      <h3>🩺 의료 정보 전달 효율화</h3>
      <ul>
        <li>진료 및 검사 일정, 검진 전 주의사항 등을 환자와 간병인에게 사전 알림</li>
        <li>의료진이 반복적인 문의와 절차에서 벗어나 본연의 의료 서비스에 집중할 수 있도록 지원</li>
      </ul>
    </td>
    <td style="padding: 15px; vertical-align: top;">
      <h3>📚 AI 기반 지능형 환자 케어 시스템</h3>
      <ul>
        <li>Chat GPT Open API 활용 챗봇으로 24시간 정보 제공 및 기초 상담 지원</li>
        <li>환자-의료진 간 실시간 소통 채널 확보</li>
        <li>병원 정보 및 저장된 데이터를 기반으로 맞춤형 응답 제공</li>
        <li>중요 정보의 실시간 푸시 알림 서비스</li>
      </ul>
    </td>
  </tr>
</table>

<br>

## 💡 서비스 흐름도

```mermaid
graph TD
    A[환자/간병인 요청] --> B[AI 분석/분류]
    B --> C[적절한 대응 경로 배정]
    C --> D[콜벨 요청]
    C --> E[일반 문의]
    C --> F[의료 상담]
    D --> G[담당 의료진에게 즉시 전달]
    G --> H[의료진 응대]
    E --> I[의료진과 실시간 소통]
    F --> J[GPT LLM 기반 프롬프트 활용]
    J --> K[병원 정보 및 의료 지식 자동 응답]

    style A fill:#f9f9f9,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style B fill:#e6f7ff,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style C fill:#e6f7ff,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style D fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style E fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style F fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style G fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style H fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style I fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style J fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
    style K fill:#f0f0f0,stroke:#333,stroke-width:1px,color:#000000,font-weight:bold
```

<br>

## 🛠️ 기술 스택

<div align="center">

| 분류 | 기술 |
|:---:|:---:|
| **프레임워크** | <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" height="25"> <img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=java&logoColor=white" height="25"> |
| **데이터베이스** | <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" height="25"> <img src="https://img.shields.io/badge/JPA/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" height="25"> |
| **보안** | <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" height="25"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" height="25"> |
| **실시간 통신** | <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white" height="25"> <img src="https://img.shields.io/badge/STOMP-000000?style=for-the-badge" height="25"> |
| **알림 서비스** | <img src="https://img.shields.io/badge/Firebase_Cloud_Messaging-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" height="25"> |
| **API 문서화** | <img src="https://img.shields.io/badge/Swagger/OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" height="25"> |
| **AI 통합** | <img src="https://img.shields.io/badge/ChatGPT_API-74aa9c?style=for-the-badge&logo=openai&logoColor=white" height="25"> |

</div>

<br>

## 📊 기대 효과

<div align="center">
<table>
  <tr>
    <td align="center"><b>👨‍⚕️<br>환자 만족도 향상</b></td>
    <td>신속한 응대와 개인화된 서비스 제공</td>
  </tr>
  <tr>
    <td align="center"><b>🏥<br>의료진 업무 효율화</b></td>
    <td>반복 업무 감소 및 핵심 의료 서비스 집중</td>
  </tr>
  <tr>
    <td align="center"><b>⚙️<br>병원 운영 최적화</b></td>
    <td>자원 배분 효율화 및 서비스 질 향상</td>
  </tr>
  <tr>
    <td align="center"><b>🔬<br>디지털 헬스케어 혁신</b></td>
    <td>스마트 병원 환경 구축 기여</td>
  </tr>
</table>
</div>

<br>

## 📖 API 문서

<div align="center">
  
[![API 문서](https://img.shields.io/badge/Swagger_UI-API_문서_바로가기-85EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black)](https://carebridge.kro.kr/swagger-ui/index.html)

</div>

<br>

## 🧑‍💻 개발팀

<div align="center">
  
> CareBridge는 한양대학교 ERICA 캠퍼스 캡스톤 프로젝트로 개발되었습니다.

| 이름 | 역할 |
|:---:|:---:|
| 박세현 | 백엔드 개발 |
| 성동진 | 백엔드 개발 |
| 강민경 | 프론트엔드 개발 |
| 김채현 | 프론트엔드 개발 |
| 문민영 | 프론트엔드 개발 |

</div>

<br>

## 📄 라이센스

<div align="center">
  
본 프로젝트는 비공개 캡스톤 프로젝트로, 모든 권리가 개발팀에게 있습니다.

© 2024 CareBridge Team. All Rights Reserved.

</div>

