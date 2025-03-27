package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class State {
    @Id
    private String id = "javers";
    private List<Resource> resources = new ArrayList<>();

    public State() {
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }
}
