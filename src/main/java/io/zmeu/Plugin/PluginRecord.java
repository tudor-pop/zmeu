package io.zmeu.Plugin;

import io.zmeu.api.Plugin;
import io.zmeu.api.Provider;
import org.pf4j.PluginWrapper;

public record PluginRecord(Provider provider, PluginWrapper plugin, ClassLoader classLoader) {
}
