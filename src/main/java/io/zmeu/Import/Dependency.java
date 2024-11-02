package io.zmeu.Import;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public record Dependency(@Nullable String uri, String name, String version) {
    public Dependency(String name, String version) {
        this("", name, version);
    }

    public Dependency(@Nullable String uri) {
        this(uri, "", "");
    }

    public Dependency(@Nullable String uri, String name, String version) {
        this.uri = uri;
        this.name = name;
        this.version = version;
    }

    URI toUri() {
        if (uri.startsWith("https") || uri.startsWith("http")) {
            return URI.create(uri);
        }
        return URI.create("file:///" + uri);
    }
}
