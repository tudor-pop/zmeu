package io.zmeu.Runtime;

import io.zmeu.Runtime.Values.Deferred;
import io.zmeu.Runtime.Values.DeferredObserverValue;
import io.zmeu.Runtime.Values.ResourceValue;

import java.util.*;

public class DeferedObservable {
    private final Map<String, Set<DeferredObserverValue>> deferredResources = new HashMap<>();

    public void notifyObservers(Interpreter interpreter, String resourceName) {
        // if there are observers waiting to be notified
        var observers = deferredResources.get(resourceName);
        if (observers == null) {
            // reached when a resource gets fully evaluated and doesn't have any observers waiting to be evaluated
            return;
        }

        for (DeferredObserverValue it : observers) {
            it.notify(interpreter);
        }

        observers.removeIf(DeferredObserverValue::isEvaluated);
        if (observers.isEmpty()) {
            // clean up the key as well when there are no longer resources interested in being notified by this resource
            deferredResources.remove(resourceName);
        }
    }

    public void removeObserver(DeferredObserverValue it, ResourceValue resourceValue) {
        for (String dependency : resourceValue.getDependencies()) {
            var dependencies = deferredResources.get(dependency);
            dependencies.remove(it);
        }
    }

    public void addObserver(DeferredObserverValue resource, Deferred deferred) {
        var observers = deferredResources.get(deferred.resource());
        if (observers == null) {
            observers = new HashSet<>();
            deferredResources.put(deferred.resource(), observers);
        }
        observers.add(resource);
    }
}
