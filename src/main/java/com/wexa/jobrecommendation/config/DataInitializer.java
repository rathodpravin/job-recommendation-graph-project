package com.wexa.jobrecommendation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Neo4jClient neo4jClient;

    public DataInitializer(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {
        try {
            // Clear existing data safely
            neo4jClient.query("MATCH (n) DETACH DELETE n").run();

            // Seed sample graph data
            String seedCypher = 
                "CREATE (java:Skill {name: 'Java'}), " +
                "       (spring:Skill {name: 'Spring Boot'}), " +
                "       (cypher:Skill {name: 'Cypher'}), " +
                "       (u1:User {id: 'u101', name: 'Alice'}), " +
                "       (u1)-[:HAS_SKILL]->(java), " +
                "       (u1)-[:HAS_SKILL]->(spring), " +
                "       (job1:JobRole {id: 'j201', title: 'Backend Engineer'}), " +
                "       (job1)-[:REQUIRES_SKILL]->(java), " +
                "       (job1)-[:REQUIRES_SKILL]->(spring), " +
                "       (job2:JobRole {id: 'j202', title: 'Graph Database Architect'}), " +
                "       (job2)-[:REQUIRES_SKILL]->(java), " +
                "       (job2)-[:REQUIRES_SKILL]->(cypher)";

            neo4jClient.query(seedCypher).run();
            System.out.println(">>> Seed Data successfully injected into CognoDB!");
        } catch (Exception e) {
            System.err.println(">>> WARNING: Could not seed database on startup: " + e.getMessage());
        }
    }
}