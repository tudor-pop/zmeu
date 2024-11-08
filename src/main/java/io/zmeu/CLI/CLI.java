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
import lombok.extern.log4j.Log4j2;
import org.javers.core.Javers;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

import static io.zmeu.CLI.FileHelpers.loadZuFiles;
import static picocli.CommandLine.Parameters;

@Command(name = "cio", mixinStandardHelpOptions = true, version = "cio 1.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
@Log4j2
class CLI implements Runnable {

    @Parameters(paramLabel = "SourceFiles", description = "The source file")
    private File[] sourceFiles;

    private final Zmeu zmeu;


    @SneakyThrows
    public CLI() {
        zmeu = new Zmeu();
    }

    @SneakyThrows
    @Override
    public void run() { // your business logic goes here...
        zmeu.run();
    }

    public static void main(String... args) {
        var commandLine = new CommandLine(new CLI());
        commandLine.execute(args);
    }
}