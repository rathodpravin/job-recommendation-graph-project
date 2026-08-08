# Enterprise Job Recommendation Engine (Graph Platform)

An enterprise-ready **Spring Boot 3** REST API powered by **Spring Data Neo4j**, **Cypher Graph Traversal**, and **CognoDB Cloud**. This backend engine analyzes candidate skills against open job requirements to perform real-time skill-gap analyses and percentage-match scoring.

---

## 🛠️ Technology Stack

* **Java**: 21
* **Framework**: Spring Boot 3.2.3
* **Data Layer**: Spring Data Neo4j (SDN 7.x), Neo4j Java Driver 5.x
* **Graph Database**: CognoDB Cloud / Neo4j Graph DB (Bolt Protocol over TLS)
* **API Documentation**: SpringDoc OpenAPI 3.0 / Swagger UI
* **Build Tool**: Maven

---

## 📁 Package Architecture

```text
com.wexa.jobrecommendation
 ├── config/
 │    ├── DatabaseSeeder.java       # Startup seeder using Cypher MERGE queries
 │    ├── Neo4jConfig.java          # Primary PlatformTransactionManager configuration
 │    └── WebConfig.java             # Global CORS mapping for React frontend
 ├── controller/
 │    └── RecommendationController.java # Swagger-annotated REST controller
 ├── dto/
 │    ├── JobMatchResultDto.java    # Output DTO with match % & missing skills
 │    └── UserCreateRequestDto.java # Input payload for candidate ingestion
 ├── model/
 │    ├── JobRole.java              # @Node entity for job positions
 │    ├── Skill.java                # @Node entity for technical skill sets
 │    └── User.java                 # @Node entity for candidate profiles
 ├── repository/
 │    └── UserRepository.java       # Spring Data Neo4j interface
 └── service/
      ├── RecommendationService.java
      └── impl/
           └── RecommendationServiceImpl.java # Cypher execution & matching logic