package io.zmeu.Runtime.Values;

import io.zmeu.Runtime.Interpreter;

public interface DeferredObserverValue {
    void update(Interpreter interpreter);

}
