package io.zmeu.Plugin;

import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import io.zmeu.api.Schemas;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PluginFactory {
    @Getter
    private final List<Schemas> schemas = new ArrayList<>();
    @Getter
    private StringBuilder schemasString = new StringBuilder();

    public CustomPluginManager create(Zmeufile zmeufile) {
        var pluginManager = new CustomPluginManager(zmeufile.pluginsPath());
        pluginManager.loadPlugins();

//        pluginManager.enablePlugin("welcome-plugin");

        pluginManager.startPlugins();

        // print extensions for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            log.info("Extensions added by plugin {}", pluginId);
            var extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                log.info("\t\t{}", extension);
            }
        }

        log.info("Plugin directory: {}", pluginManager.getPluginsRoot());
        List<Provider> providers = pluginManager.getExtensions(Provider.class);
        log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());

        for (var provider : providers) {
            log.info("Resources: ");
            provider.resources()
                    .list()
                    .forEach(message -> log.info("\t{}", message));

            schemas.add(provider.schemas());

            schemasString.append(provider.schemasString());
        }


        // stop the plugins
//        pluginManager.stopPlugins();
        return pluginManager;
    }
}
