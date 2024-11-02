package io.zmeu.Import;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record Zmeufile(
        Dependencies dependencies,
        Path pluginsPath
) {
    public Zmeufile(Dependencies dependencies, Path pluginsPath) {
        this.dependencies = dependencies;
        if (Files.notExists(pluginsPath)) {
            try {
                Files.createDirectories(pluginsPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.pluginsPath = pluginsPath;
    }

    public Zmeufile(Dependencies dependencies) {
        this(dependencies, Path.of(URI.create("file:///" + Paths.get(".zmeu/plugins").toAbsolutePath())));
    }
}
