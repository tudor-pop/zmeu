package io.zmeu;

import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.api.Provider;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.pf4j.PluginWrapper;

import java.util.List;

@Log4j2
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

        run();

//        System.out.println("REPL FCL v0.1.0");
//
//        var mapper = new ObjectMapper();
//
//        var interpreter = new Interpreter();
//        var scanner = new Scanner(System.in);
//        while (true) {
//            var tokenizer = new Tokenizer();
//            var parser = new Parser();
//            System.out.print("> ");
//            var line = scanner.nextLine();
//            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("exit()") ||
//                line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("quit()")) {
//                System.exit(0);
//            }
//            Program program = parser.produceAST(tokenizer.tokenize(line));
//            System.out.println(mapper.writeValueAsString(program));
//            var evalRes = interpreter.eval(program);
//            log.debug("{}", evalRes);
//            log.debug("\n\n");
//        }

    }

    public static void run() {
        var pluginManager = new CustomPluginManager();
        // load the plugins
        pluginManager.loadPlugins();

        // enable a disabled plugin
//        pluginManager.enablePlugin("welcome-plugin");

        // start (active/resolved) the plugins
        pluginManager.startPlugins();

        log.info("Plugin directory: " + pluginManager.getPluginsRoot());
        // retrieves the extensions for Greeting extension point
        List<Provider> providers = pluginManager.getExtensions(Provider.class);
        log.info(String.format("Found %d extensions for extension point '%s'", providers.size(), Provider.class.getName()));
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