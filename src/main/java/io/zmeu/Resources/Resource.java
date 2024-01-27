package io.zmeu.Resources;

import lombok.Getter;

import java.util.UUID;

public class Resource {
    @Getter
    private final UUID rid = UUID.randomUUID();
}
