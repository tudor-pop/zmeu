package io.zmeu.Engine;

import io.zmeu.Resources.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Dag {
    private final List<Resource> resources = new ArrayList<>();
    private final Map<UUID, Resource> edge = new ConcurrentHashMap<>();


}
