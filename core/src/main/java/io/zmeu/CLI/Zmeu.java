package io.zmeu.CLI;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.Tokenizer;
import io.zmeu.Frontend.Parser.Parser;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Plugin.Providers;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Interpreter;
import io.zmeu.TypeChecker.TypeChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static io.zmeu.CLI.FileHelpers.loadZuFiles;

public class Zmeu {
    private final Interpreter interpreter;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final TypeChecker typeChecker;
    private final Providers providers;
    private final Javers javers;
    private final Diff diff;
    private final ResourceManager resourceManager;
    private final ObjectMapper objectMapper;

    public Zmeu() throws IOException {
        this.providers = new Providers(ZmeuInjector.createZmeufile());

        this.objectMapper = ZmeuInjector.createMapper();
        this.javers = JaversFactory.createNoDb();
        this.diff = new Diff(javers);

        this.interpreter = new Interpreter(new Environment<>());
        this.tokenizer = new Tokenizer();
        this.parser = new Parser();
        this.typeChecker = new TypeChecker();
        this.resourceManager = new ResourceManager(providers, objectMapper, diff);
    }

    @SneakyThrows
    public void run() {
        this.providers.loadPlugins();
        var schemasString = new StringBuilder(providers.schemas());
        var byFileName = loadZuFiles();
        for (var file : byFileName) {
            var resources = Files.readString(file.toPath());
            schemasString.append(resources);
        }

        List<Token> tokens = tokenizer.tokenize(schemasString.toString());

        Program program = parser.produceAST(tokens);
        typeChecker.visit(program);
        var evalRes = interpreter.visit(program);
        var resources = interpreter.getResources();
        var plan = resourceManager.plan(resources);

        resourceManager.apply(plan);
    }
}
