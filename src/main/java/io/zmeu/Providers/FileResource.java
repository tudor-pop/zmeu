package io.zmeu.Providers;

import org.pf4j.ExtensionPoint;

import java.nio.file.Path;

public interface FileResource extends ExtensionPoint {

    String read(Path path);

    void write(Path path, String content);


}