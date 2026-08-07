package com.wexa.jobrecommendation.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Skill")
public class Skill {

	@Id
    private String name;

    public Skill() {}
    public Skill(String name) { this.name = name; }

    public String getName() { return name; }
}
