package dev.fangscl.Plugin.config;

import org.pf4j.DefaultPluginDescriptor;

public class CustomPluginDescriptor extends DefaultPluginDescriptor {

    public CustomPluginDescriptor() {
        super();
    }

    public CustomPluginDescriptor(String pluginId, String pluginDescription, String pluginClass, String version, String requires, String provider, String license) {
        super(pluginId, pluginDescription, pluginClass, version, requires, provider, license);
        this.setPluginId(pluginId + version);
    }
}
