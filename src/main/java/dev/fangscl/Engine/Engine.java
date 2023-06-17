package dev.fangscl.Engine;

import dev.fangscl.Resources.Resource;
import dev.fangscl.Resources.Vm;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Map;

public class Engine {
    private final DirectedAcyclicGraph<Resource, Resource> dag = new DirectedAcyclicGraph<>(Resource.class);


    public void process(String resType, Map<String, Object> variables) {
        switch (resType) {
            case "Vm" -> {
                var resource = new Vm();
                dag.addVertex(resource);
            }
        }
    }
}
