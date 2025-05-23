package io.zmeu.file;

import io.zmeu.api.Provider;
import io.zmeu.api.Resource;
import io.zmeu.api.Resources;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@Extension
public class FileProvider extends Provider<File> {

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
            throw new IllegalArgumentException("Path and name can't be null at the same time: " + resource);
        }
    }

    @Override
    public File create(File resource) {
        requirePathOrName(resource);
        return writeFile(resource, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    @Override
    public File update(File oldResource, File newResource) {
        requirePathOrName(oldResource);
        requirePathOrName(newResource);

        try {
            if (!oldResource.path().equals(newResource.path())) {
                Files.move(Paths.get(oldResource.getPath()), Paths.get(newResource.getPath()), StandardCopyOption.REPLACE_EXISTING);
            } else if (oldResource.getContent().length() != newResource.getContent().length()) {
                writeFile(newResource, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            }
        } catch (RuntimeException | IOException e) {
            return newResource;
        }
        return newResource;
    }

    private static File writeFile(File resource, StandardOpenOption... options) {
        try (var writer = Files.newBufferedWriter(resource.path(), StandardCharsets.UTF_8, options)) {
            writer.write(resource.getContent());
            return resource;
        } catch (RuntimeException | IOException e) {
            return resource;
        }
    }

    @Override
    public boolean delete(File resource) {
        requirePathOrName(resource);
        if (resource.getPath() == null) {
            return Paths.get(resource.getName()).toFile().delete();
        } else if (resource.getName() == null) {
            return Paths.get(resource.getPath()).toFile().delete();
        } else {
            return resource.path().toFile().delete();
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