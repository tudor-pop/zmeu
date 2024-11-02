package io.zmeu.Plugin;

import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pf4j.PluginWrapper;

import java.util.List;

@Log4j2
@AllArgsConstructor
public class PluginFactory {

    public static CustomPluginManager create(Zmeufile zmeufile) {
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
                log.info("\t\t" + extension);
            }
        }

        log.info("Plugin directory: {}", pluginManager.getPluginsRoot());
        List<Provider> providers = pluginManager.getExtensions(Provider.class);
        log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());

        for (var provider : providers) {
            log.info("Resources: ");
            provider.resources()
                    .list()
                    .forEach(message -> log.info("\t" + message));
//            var read = provider.read(FileResource.builder()
//                    .name("fisier.txt")
//                    .path("./")
//                    .build());
        }


        // stop the plugins
//        pluginManager.stopPlugins();
        return pluginManager;
    }
}
