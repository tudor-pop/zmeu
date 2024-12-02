package io.zmeu.Plugin;

import io.zmeu.Import.Dependency;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import io.zmeu.api.schema.SchemaDefinition;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class PluginFactory {
    @Getter
    private final HashMap<String, PluginRecord> pluginHashMap = new HashMap<>();
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
            Path resolve = pluginPath.resolve(dependency.versionedName());
            if (!resolve.toFile().exists()) {
                throw new IllegalArgumentException(dependency.versionedName() + " does not exist!");
            }
            pluginManager.loadPlugin(resolve);
        }

        log.info("Plugin directory: {}", pluginManager.getPluginsRoot());
//        pluginManager.enablePlugin("welcome-plugin");

        pluginManager.startPlugins();

        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
//            ClassLoader pluginClassLoader = pluginManager.getPluginClassLoader(pluginId);

            log.info("Extensions added by plugin {}", pluginId);

            List<Provider> providers = pluginManager.getExtensions(Provider.class);
            log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());
            for (var provider : providers) {
                log.info("Loading provider {}", provider.getClass().getName());
//                var loadedClass = pluginClassLoader.loadClass(provider.resourceType());

                for (SchemaDefinition schema : provider.schemas().getItems()) {
                    var record = this.pluginHashMap.get(schema.getName());
                    if (record == null) {
                        this.pluginHashMap.put(schema.getName(), new PluginRecord(provider, plugin));
                    }
                }
            }
        }


        // stop the plugins
//        pluginManager.stopPlugins();
        return pluginManager;
    }
}
