package io.zmeu.Plugin;

import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import lombok.extern.log4j.Log4j2;
import org.pf4j.PluginWrapper;

import java.util.List;

@Log4j2
public class PluginFactory {
    public static void create() {
        var pluginManager = new CustomPluginManager();
        pluginManager.loadPlugins();

//        pluginManager.enablePlugin("welcome-plugin");

        pluginManager.startPlugins();

        log.info("Plugin directory: " + pluginManager.getPluginsRoot());
        List<Provider> providers = pluginManager.getExtensions(Provider.class);
        log.info("Found {} extensions for extension point '{}'", providers.size(), Provider.class.getName());

        for (var provider : providers) {
            log.info(">>> " + provider.getResources());
//            declaration.setContent(Attribute.builder().value().build());
//            try {
//                var read = provider.read(declaration);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
        }

        // print extensions for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            log.info(String.format("Extensions added by plugin '%s':", pluginId));
            log.info(String.format("Extensions added by plugin version '%s':", plugin.getDescriptor().getVersion()));
            var extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                log.info("   " + extension);
            }
        }

        // stop the plugins
        pluginManager.stopPlugins();
    }
}
