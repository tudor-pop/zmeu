package io.zmeu.CLI;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.Tokenizer;
import io.zmeu.Frontend.Parser.Parser;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Interpreter;
import io.zmeu.TypeChecker.TypeChecker;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static picocli.CommandLine.Parameters;

@Command(name = "cio", mixinStandardHelpOptions = true, version = "cio 1.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
@Log4j2
class CLI implements Runnable {
    public static final int LAST_MODIFIED_IN_MINUTES = 50000;

    @Parameters(paramLabel = "SourceFiles", description = "The source file")
    private File[] sourceFiles;
    private Zmeufile zmeufile;
    private PluginFactory pluginFactory = new PluginFactory();

    @SneakyThrows
    @Override
    public void run() { // your business logic goes here...
        var mapper = new YAMLMapper();
        var zmeufilePath = Paths.get(URI.create("file://" + Paths.get("Zmeufile.yml").toAbsolutePath()));
        var zmeufileContent = Files.readString(zmeufilePath);
        var dependencies = mapper.readValue(zmeufileContent, Dependencies.class);
        this.zmeufile = new Zmeufile(dependencies);
        pluginFactory.create(zmeufile);

        var interpreter = new Interpreter();
        var tokenizer = new Tokenizer();
        var parser = new Parser();
        var typeChecker = new TypeChecker();

        StringBuilder schemasString = pluginFactory.getSchemasString();
        var resources = """
                resource File users {
                    name = "users"
                    content = "tudor"
                    path = "./"
                }
                """;
        schemasString.append(resources);
        List<Token> tokens = tokenizer.tokenize(schemasString.toString());

        Program program = parser.produceAST(tokens);
        typeChecker.eval(program);
        var evalRes = interpreter.eval(program);
        System.out.println(evalRes);
    }

    public File[] findByFileName(Path path, String fileName) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.zmeu");
        PathMatcher ignore = FileSystems.getDefault().getPathMatcher("glob:./{build,gradle,.gradle,git,.git}**");
        try (Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE, (p, basicFileAttributes) -> {
            if (Files.isDirectory(p) || !Files.isReadable(p)) {
                return false;
            }
            if (ignore.matches(p)) {
                return false;
            }
            if (!matcher.matches(p)) {
                return false;
            }
            var fileTime = basicFileAttributes.lastModifiedTime();
            // negative if less, positive if greater
            // 1 means fileTime equals or after the provided instant argument
            // -1 means fileTime before the provided instant argument
            return ChronoUnit.MINUTES.between(fileTime.toInstant(), Instant.now()) <= LAST_MODIFIED_IN_MINUTES;
        })) {
            return pathStream.map(Path::toFile).toArray(File[]::new);
        }
    }


    public static void main(String... args) {
        var commandLine = new CommandLine(new CLI());
        commandLine.execute(args);
    }
}