package io.zmeu.Plugin;

import io.zmeu.Import.Dependency;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import io.zmeu.api.schema.SchemaDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class PluginFactory {
    @Getter
    private final HashMap<String, Provider> pluginHashMap = new HashMap<>();
    @Getter
    @Setter
    private DefaultPluginManager pluginManager;
    private final Zmeufile zmeufile;

    public PluginFactory(Zmeufile zmeufile) {
        this.zmeufile = zmeufile;
        this.pluginManager = new CustomPluginManager(zmeufile.pluginsPath());
    }

    @SneakyThrows
    public DefaultPluginManager loadPlugins() {
        Path pluginPath = zmeufile.pluginsPath();
        for (Dependency dependency : zmeufile.dependencies().dependencies()) {
            Path resolve = pluginPath.resolve(dependency.versionedName());
            if (!resolve.toFile().exists()) {
                throw new IllegalArgumentException(dependency.versionedName() + " does not exist!");
            }
            pluginManager.loadPlugin(resolve);
        }

        log.info("Plugin directory: {}", pluginManager.getPluginsRoot());
//        pluginManager.enablePlugin("welcome-plugin");
//        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
//            ClassLoader pluginClassLoader = pluginManager.getPluginClassLoader(pluginId);
            log.info("Extensions added by plugin {}", pluginId);
        }

        List<Provider> providers = pluginManager.getExtensions(Provider.class);
        log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());
        for (var provider : providers) {
            log.info("Loading provider {}", provider.getClass().getName());
//                var loadedClass = pluginClassLoader.loadClass(provider.resourceType());
            var schema = provider.schema();
            this.pluginHashMap.putIfAbsent(schema.getName(), provider);
        }


        // stop the plugins
//        pluginManager.stopPlugins();
        return pluginManager;
    }

    public void putProvider(String key, Provider provider) {
        this.pluginHashMap.put(key, provider);
    }

    public void stopPlugins() {
        pluginManager.stopPlugins();
    }

    public Provider getProvider(String provider) {
        return pluginHashMap.get(provider);
    }

    public List<Provider> getProviders() {
        return pluginManager.getExtensions(Provider.class);
    }

    public String schemas() {
        return pluginHashMap.values()
                .stream()
                .map(Provider::schemasString)
                .collect(Collectors.joining());
    }
}
