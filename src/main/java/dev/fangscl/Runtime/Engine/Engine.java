package dev.fangscl.Runtime.Engine;

import dev.fangscl.Resources.Resource;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Map;

public class Engine {
    private final DirectedAcyclicGraph<Resource, Resource> dag = new DirectedAcyclicGraph<>(Resource.class);


    public void process(String nameString, Map<String, Object> variables) {

    }
}
