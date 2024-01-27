package dev.fangscl.Plugin.config;

import org.pf4j.CompoundPluginDescriptorFinder;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PropertiesPluginDescriptorFinder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CustomPluginManager extends DefaultPluginManager {

    public CustomPluginManager() {
        super();
    }

    public CustomPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }
    public CustomPluginManager(String... pluginsRoots) {
        this(
                Arrays.stream(pluginsRoots)
                        .map(Paths::get)
                        .map(Path::toAbsolutePath)
                        .toList()
        );
    }

    public CustomPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
        System.out.println(pluginsRoots);
    }

    @Override
    protected CompoundPluginDescriptorFinder createPluginDescriptorFinder() {
        return new CompoundPluginDescriptorFinder()
                // Demo is using the Manifest file
                // PropertiesPluginDescriptorFinder is commented out just to avoid error log
                .add(new PropertiesPluginDescriptorFinder())
                .add(new CustomPluginDescriptorFinder());
    }
}
