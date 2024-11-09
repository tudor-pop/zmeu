package io.zmeu.file;

import io.zmeu.api.Provider;
import io.zmeu.api.Resources;
import org.pf4j.Extension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Extension
public class FileProvider implements Provider<File> {

    @Override
    public Resources<File> resources() {
        var resource = new File();

        return new Resources<>(List.of(resource));
    }

    @Override
    public File read(File resource) {
        requirePathOrName(resource);
        try {
            if (resource.getPath() == null) {
                var content = readContent(resource.getName());
                return File.builder()
                        .content(content)
                        .name(resource.getName())
                        .build();
            } else if (resource.getName() == null) {
                var content = readContent(resource.getPath());
                return File.builder()
                        .content(content)
                        .path(resource.getPath())
                        .build();
            } else {
                var content = readContent(resource.getPath() + resource.getName());
                return File.builder()
                        .content(content)
                        .name(resource.getName())
                        .path(resource.getPath())
                        .build();
            }
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static void requirePathOrName(File resource) {
        if (resource.getPath() == null && resource.getName() == null) {
            throw new IllegalArgumentException("Path and name can't be null at the same time");
        }
    }

    @Override
    public File create(File resource) {
        requirePathOrName(resource);
        try {
            if (resource.getPath() == null) {
                try (var fileWritter = Files.newBufferedWriter(Paths.get(resource.getName()),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                    fileWritter.write(resource.getContent());
                }
            } else if (resource.getName() == null) {
                try (var fileWritter = Files.newBufferedWriter(Paths.get(resource.getPath()),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                    fileWritter.write(resource.getContent());
                }
            } else {
                String filename = resource.getPath() + resource.getName();
                try (var fileWritter = Files.newBufferedWriter(Paths.get(filename),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                    fileWritter.write(resource.getContent());
                }
            }
            return resource;
        } catch (RuntimeException | IOException e) {
            return resource;
        }
    }

    @Override
    public boolean remove(File resource) {
        requirePathOrName(resource);
        if (resource.getPath() == null) {
            return Paths.get(resource.getName()).toFile().delete();
        } else if (resource.getName() == null) {
            return Paths.get(resource.getPath()).toFile().delete();
        } else {
            String filename = resource.getPath() + resource.getName();
            return Paths.get(filename).toFile().delete();
        }
    }

    @Override
    public String namespace() {
        return "File";
    }

    @Override
    public String resourceType() {
        return File.class.getName();
    }

    private static String readContent(String filename) {
        try {
            return Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}