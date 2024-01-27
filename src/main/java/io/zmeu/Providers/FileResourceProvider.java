package io.zmeu.Providers;

import org.pf4j.Extension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Extension
public class FileResourceProvider implements FileResource {


    @Override
    public String read(Path path) {
        File file = path.toFile();
        if (file.exists()) {
            if (file.isDirectory()) {
                return null;
            }
            if (file.canRead()) {
                try {
                    return Files.readString(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public void write(Path path, String content) {

    }
}