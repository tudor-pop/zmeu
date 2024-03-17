package io.zmeu.Import;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

public record Dependency(@NotNull String uri) {
    URI toUri() {
        if (uri.startsWith("https") || uri.startsWith("http")) {
            return URI.create(uri);
        }
        return URI.create("file:///" + uri);
    }
}
