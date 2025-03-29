package io.zmeu.Runtime;

import io.zmeu.Runtime.Values.Deferred;
import io.zmeu.Runtime.Values.DeferredObserverValue;
import io.zmeu.Runtime.Values.ResourceValue;

import java.util.*;

/**
 * The class registers observables interested in a resource evaluation.
 * It is needed in resource dependency resolution.
 * If a resource has dependencies it cannot access the dependency properties because those were not evaluated yet
 * so we subscribe to be notified when that resource gets evaluated
 * Let's say 2 instances need to have the same name:
 * resource Instance a {
 *    name = [Instance.b.name]
 * }
 * resource Instance b { }
 * --------- on b's evaluation -----------
 * "b" ---- notifies resources ----> [a]
 * ---------------------------------------
 * now A can be reevaluated
 */
public class DeferredObservable {
    private final Map<String, Set<DeferredObserverValue>> deferredResources = new HashMap<>();

    /**
     * Function called each time a resource is fully evaluated in order to notify anyone interested in this event.
     */
    public void notifyObservers(Interpreter interpreter, String resourceName) {
        // if there are observers waiting to be notified
        // the map is checked each time a resource is evaluated
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
