package io.zmeu.CLI;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.Engine;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.Tokenizer;
import io.zmeu.Frontend.Parser.Parser;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Interpreter;
import io.zmeu.TypeChecker.TypeChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.zmeu.CLI.FileHelpers.loadZuFiles;

public class Zmeu {
    private final Interpreter interpreter;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final TypeChecker typeChecker;
    private final Zmeufile zmeufile;
    private final PluginFactory pluginFactory;
    private final Javers javers;
    private final Diff diff;
    private final YAMLMapper mapper;

    public Zmeu() throws IOException {
        mapper = new YAMLMapper();
        var zmeufilePath = Paths.get(URI.create("file://" + Paths.get("Zmeufile.yml").toAbsolutePath()));
        var zmeufileContent = Files.readString(zmeufilePath);
        var dependencies = mapper.readValue(zmeufileContent, Dependencies.class);
        this.zmeufile = new Zmeufile(dependencies);
        this.pluginFactory = new PluginFactory(zmeufile);

        this.javers = JaversFactory.create("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
        this.diff = new Diff(javers);

        this.pluginFactory.loadPlugins();
        this.interpreter = new Interpreter(new Environment<>(), new Engine(pluginFactory, mapper, diff, javers));
        this.tokenizer = new Tokenizer();
        this.parser = new Parser();
        this.typeChecker = new TypeChecker();
    }

    @SneakyThrows
    public void run() {
        var schemasString = pluginFactory.getSchemasString();
        var byFileName = loadZuFiles();
        for (var file : byFileName) {
            var resources = Files.readString(file.toPath());
            schemasString.append(resources);
        }

        List<Token> tokens = tokenizer.tokenize(schemasString.toString());

        Program program = parser.produceAST(tokens);
        typeChecker.eval(program);
        var evalRes = interpreter.eval(program);
        System.out.println(evalRes);

    }
}
