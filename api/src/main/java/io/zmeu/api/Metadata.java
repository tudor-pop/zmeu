package io.zmeu.api;

import lombok.Data;

import java.net.URI;

@Data
public class Metadata {
    private String typeName;
    private URI location;

    public Metadata(String typeName) {
        this.typeName = typeName;
    }

    public Metadata(URI location) {
        this.location = location;
    }

    public Metadata() {
    }

    public Metadata(String typeName, URI location) {
        this.typeName = typeName;
        this.location = location;
    }
}
