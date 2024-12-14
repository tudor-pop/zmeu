package io.zmeu.CLI;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.Tokenizer;
import io.zmeu.Frontend.Parser.Parser;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Interpreter;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.TypeChecker.TypeChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static io.zmeu.CLI.FileHelpers.loadZuFiles;

public class Zmeu {
    private final Interpreter interpreter;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final TypeChecker typeChecker;
    private final PluginFactory pluginFactory;
    private final Javers javers;
    private final Diff diff;
    private final ResourceManager resourceManager;
    private final ObjectMapper objectMapper;

    public Zmeu() throws IOException {
        this.pluginFactory = new PluginFactory(ZmeuInjector.createZmeufile());

        this.objectMapper = ZmeuInjector.createMapper();
        this.javers = JaversFactory.create("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
        this.diff = new Diff(javers, new ModelMapper());

        this.interpreter = new Interpreter(new Environment<>());
        this.tokenizer = new Tokenizer();
        this.parser = new Parser();
        this.typeChecker = new TypeChecker();
        this.resourceManager = new ResourceManager(pluginFactory, objectMapper, diff);
    }

    @SneakyThrows
    public void run() {
        this.pluginFactory.loadPlugins();
        var schemasString = new StringBuilder(pluginFactory.getPluginHashMap().values().stream().map(it -> it.provider().schemasString()).collect(Collectors.joining()));
        var byFileName = loadZuFiles();
        for (var file : byFileName) {
            var resources = Files.readString(file.toPath());
            schemasString.append(resources);
        }

        List<Token> tokens = tokenizer.tokenize(schemasString.toString());

        Program program = parser.produceAST(tokens);
        typeChecker.eval(program);
        var evalRes = interpreter.eval(program);
        var resources = interpreter.getEnv()
                .getVariables().values().stream()
                .filter(it-> it instanceof SchemaValue)
                .map(SchemaValue.class::cast)
                .collect(Collectors.toMap(SchemaValue::getType, SchemaValue::getInstances));
        var plan = resourceManager.plan(resources);

        resourceManager.apply(plan);
    }
}
