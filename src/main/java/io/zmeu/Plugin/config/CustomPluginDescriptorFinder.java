package io.zmeu.Plugin.config;

import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.ManifestPluginDescriptorFinder;

public class CustomPluginDescriptorFinder extends ManifestPluginDescriptorFinder {
    @Override
    protected DefaultPluginDescriptor createPluginDescriptorInstance() {
        return new CustomPluginDescriptor();
    }
}
