package com.wexa.jobrecommendation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final Neo4jClient neo4jClient;

    public DatabaseSeeder(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {
        String cypher = 
            "MERGE (u1:User {id: 'u101'}) SET u1.name = 'Alice' " +
            "MERGE (u2:User {id: 'u102'}) SET u2.name = 'Bob' " +
            "MERGE (u3:User {id: 'u103'}) SET u3.name = 'Charlie' " +
            "MERGE (u4:User {id: 'u104'}) SET u4.name = 'Diana' " +
            "MERGE (s1:Skill {name: 'Java'}) " +
            "MERGE (s2:Skill {name: 'Spring Boot'}) " +
            "MERGE (s3:Skill {name: 'React'}) " +
            "MERGE (s4:Skill {name: 'Python'}) " +
            "MERGE (u1)-[:HAS_SKILL]->(s1) " +
            "MERGE (u1)-[:HAS_SKILL]->(s2) " +
            "MERGE (u2)-[:HAS_SKILL]->(s1) " +
            "MERGE (u2)-[:HAS_SKILL]->(s3) " +
            "MERGE (u3)-[:HAS_SKILL]->(s4) " +
            "MERGE (u4)-[:HAS_SKILL]->(s2) " +
            "MERGE (u4)-[:HAS_SKILL]->(s3)";

        neo4jClient.query(cypher).run();
        System.out.println(">>> DatabaseSeeder: Initial user nodes verified in CognoDB Cloud.");
    }
}