package dev.fangscl.Resources;

import java.util.Map;

public interface ResourceCallable {
    Object apply(String name, Map<String, Object> args);
}
