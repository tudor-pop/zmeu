package io.zmeu.Plugin;

import io.zmeu.Import.Dependency;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import io.zmeu.api.Schemas;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class PluginFactory {
    @Getter
    private final List<Schemas> schemas = new ArrayList<>();
    @Getter
    private final StringBuilder schemasString = new StringBuilder();
    @Getter
    private final HashMap<String, Provider> plugins = new HashMap<>();
    @Getter
    private final CustomPluginManager pluginManager;
    private final Zmeufile zmeufile;

    public PluginFactory(Zmeufile zmeufile) {
        this.zmeufile = zmeufile;
        this.pluginManager = new CustomPluginManager(zmeufile.pluginsPath());
    }

    @SneakyThrows
    public CustomPluginManager loadPlugins() {
        Path pluginPath = zmeufile.pluginsPath();
        for (Dependency dependency : zmeufile.dependencies().dependencies()) {
            pluginManager.loadPlugin(pluginPath.resolve(dependency.versionedName()));
        }

        log.info("Plugin directory: {}", pluginManager.getPluginsRoot());
//        pluginManager.enablePlugin("welcome-plugin");

        pluginManager.startPlugins();

        // print extensions for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            ClassLoader pluginClassLoader = pluginManager.getPluginClassLoader(pluginId);

            log.info("Extensions added by plugin {}", pluginId);

            List<Provider> providers = pluginManager.getExtensions(Provider.class);
            log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());
            for (var provider : providers) {
                log.info("Loading provider {}", provider.getClass().getName());
                var loadedClass = pluginClassLoader.loadClass(provider.resourceType());
                provider.resources().list().forEach(message -> log.info("\t{}", message));

                schemas.add(provider.schemas());
                plugins.put(provider.namespace(), provider);
                schemasString.append(provider.schemasString());
            }
        }


        // stop the plugins
//        pluginManager.stopPlugins();
        return pluginManager;
    }
}
