# 🍊 카페감수광

제주도 카페 추천 웹서비스입니다.

리뷰데이터를 카카오맵APi와 크롤링으로 수집하고 분석, 가공해 
사용자에게 선호 키워드와 위치기반으로 맞춤형 카페를 추천합니다.

![image](https://github.com/user-attachments/assets/42733447-92d6-4382-ad44-e31ba195578a)![image](https://github.com/user-attachments/assets/2a52843a-30cf-4711-8424-8ced6892d786)![image](https://github.com/user-attachments/assets/1e41d0b3-093c-4307-94a7-942c73ae8e5b)

---

## 👥 팀원

| 팀장 | 팀원 | 팀원 | 팀원 | 팀원 |
|:---:|:---:|:---:|:---:|:---:|
| [**김민준**](https://github.com/UncleSamsun) | [**김형준**](https://github.com/kimnoca) | [**우상진**](https://github.com/SangJin521) | [**전준영**](https://github.com/Isonade2) | [**정신우**](https://github.com/cupokki) |

---

## ✏️ 기술 스택

### Back-end
<img src="https://img.shields.io/badge/java-FC4C02?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"><img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elastic&logoColor=white"><img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">

### Front-end
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"><img src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"><img src="https://img.shields.io/badge/tailwindcss-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"><img src="https://img.shields.io/badge/v0-000000?style=for-the-badge&logo=v0&logoColor=white"><img src="https://img.shields.io/badge/nextdotjs-000000?style=for-the-badge&logo=nextdotjs&logoColor=white">

### 데이터 수집
<img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"><img src="https://img.shields.io/badge/selenium-43B02A?style=for-the-badge&logo=selenium&logoColor=white"><img src="https://img.shields.io/badge/keybert-000080?style=for-the-badge&logo=keybert&logoColor=white"><img src="https://img.shields.io/badge/fastapi-009688?style=for-the-badge&logo=fastapi&logoColor=white">

### 모니터링/로깅
<img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"><img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"><img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elastic&logoColor=white"><img src="https://img.shields.io/badge/logstash-005571?style=for-the-badge&logo=logstash&logoColor=white"><img src="https://img.shields.io/badge/kibana-005571?style=for-the-badge&logo=kibana&logoColor=white">

### Test
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"><img src="https://img.shields.io/badge/mockito-009639?style=for-the-badge&logo=mockito&logoColor=white">

### Deploy
<img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/Github_Actions-2088FF?style=for-the-badge&logo=GithubActions&logoColor=white"><img src="https://img.shields.io/badge/amazon_ec2-FF9900?style=for-the-badge&logo=amazon_ec2&logoColor=white"><img src="https://img.shields.io/badge/Amazon_S3-569A31?style=for-the-badge&logo=AmazonS3&logoColor=white">

### Tool
<img src="https://img.shields.io/badge/DISCORD-5865F2?style=for-the-badge&logo=discord&logoColor=white"><img src="https://img.shields.io/badge/NOTION-FFFFFF?style=for-the-badge&logo=notion&logoColor=black"><img src="https://img.shields.io/badge/Github-000000?style=for-the-badge&logo=Github&logoColor=white"/><img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/>

---

## 📜 프로젝트 목표
- 카카오맵 API 및 웹 크롤링을 활용한 카페 리뷰 및 위치 데이터 수집
- 효율적인 크롤링 및 API 호출을 위한 알고리즘 설계
- KeyBERT 등 NLP 모델을 활용한 리뷰 키워드 자동 추출 및 정제
- 다양한 소스에서 수집된 데이터를 효율적으로 저장·관리하는 데이터 통합 구조 구축
- 분산된 리뷰 데이터를 통합 관리하기 위한 데이터베이스 설계 및 연동
- Spring Batch 기반 스케줄링 시스템을 활용한 카페 키워드 자동 갱신
- 추후 자체 리뷰작성 추천 시스템을 피드백 루프에 반영하여 데이터의 신뢰성을 향상
- ElasticSearch 기반 비정형 데이터 검색 기능 및 맞춤 추천 시스템 구현

---

## 🖥️ 프로젝트 산출물

### 프로젝트 아키텍처
![image](https://github.com/user-attachments/assets/a9d162cf-c301-4e96-a308-370a888e3276)

### ERD
![image](https://github.com/user-attachments/assets/3913ece2-740c-4089-a150-a91161f1dee8)

### 요구사항 명세서
[**요구사항 명세서**](https://docs.google.com/document/d/1OxWfz5IGUoFpj1_MYIm7_L5nPWUDz5VSTac7fVRSCOc/edit?tab=t.0#heading=h.koem1lbflcn6)

### Api 명세서
![image](https://github.com/user-attachments/assets/1c43cbe5-9767-4e65-a0fc-84d426c6ce34)![image](https://github.com/user-attachments/assets/479d123e-fa85-4491-b903-95395b80b49f)

### Event Storming
[![Event_Storming](https://github.com/user-attachments/assets/f904d42d-b5de-4fda-b698-cb3c51d66025)](https://miro.com/app/board/uXjVI3UePNY=/?moveToWidget=3458764627874125631&cot=14)
