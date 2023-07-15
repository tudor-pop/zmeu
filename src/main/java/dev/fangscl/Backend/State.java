package dev.fangscl.Backend;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class State {
    private int statefileVersion;
    private String cliVersion;
    private List<JsonNode> resources = new ArrayList<>();

    public State() {
    }

    public State(JsonNode... resources) {
        Collections.addAll(this.resources, resources);
    }
}
