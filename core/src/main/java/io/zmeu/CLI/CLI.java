package io.zmeu.CLI;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;

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