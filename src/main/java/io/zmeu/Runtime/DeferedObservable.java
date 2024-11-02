package io.zmeu.Runtime;

import io.zmeu.Frontend.Parser.Expressions.ResourceExpression;
import io.zmeu.Runtime.Values.Deferred;
import io.zmeu.Runtime.Values.DeferredObserverValue;
import io.zmeu.Runtime.Values.ResourceValue;

import java.util.*;

public class DeferedObservable {
    private final Map<String, Set<DeferredObserverValue>> deferredResources = new HashMap<>();

    public void notifyObservers(Interpreter interpreter, String resourceName) {
        // if there are observers waiting to be notified
        var observers = Optional.ofNullable(deferredResources.get(resourceName)).orElse(Collections.emptySet());
        for (DeferredObserverValue it : observers) {
            var resourceValue = it.notify(interpreter);
            if (resourceValue instanceof ResourceValue value) {
                removeObserver(it, value);
            }
        }
        if (observers.isEmpty()) {
            // clean up the key as well when there are no longer resources interested in being notified by this resource
            deferredResources.remove(resourceName);
        }
    }

    public void removeObserver(DeferredObserverValue it, ResourceValue resourceValue) {
        if (it instanceof ResourceExpression resourceExpression) {
            if (resourceExpression.isEvaluated()) {
                for (String dependency : resourceValue.getDependencies()) {
                    var dependencies = deferredResources.get(dependency);
                    dependencies.remove(resourceExpression);
                }
            }
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
