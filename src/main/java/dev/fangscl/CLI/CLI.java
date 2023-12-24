package dev.fangscl.CLI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import dev.fangscl.Backend.Resource;
import dev.fangscl.Backend.State;
import dev.fangscl.Diff.Diff;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Runtime.Interpreter;
import dev.fangscl.Runtime.Values.ResourceValue;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

@Command(name = "cio", mixinStandardHelpOptions = true, version = "cio 1.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
@Log4j2
class CLI implements Runnable {
    private final static String DIR = ".cloudcode";
    private final static String STATE_FILE = DIR + "/state.yml";
    private final static String SOURCE_FILE = "main.fcl";
    public static final int LAST_MODIFIED_IN_MINUTES = 50000;

    @Option(names = {"-s", "--state"}, paramLabel = "StateFile", description = "The state file", defaultValue = STATE_FILE)
    private File state;
    @Parameters(paramLabel = "SourceFiles", description = "The source file")
    private File[] sourceFiles;


    @SneakyThrows
    @Override
    public void run() { // your business logic goes here...
        var diff = new Diff();
        createStateIfNotExists();
        var stateStr = Files.readString(state.toPath());
        var cloudStr = "";

        var mapper = new YAMLMapper();
        var interpreter = new Interpreter();
        var tokenizer = new Tokenizer();
        var parser = new Parser();
        if (sourceFiles == null) {
            sourceFiles = findByFileName(Path.of("."), ".fcl");
        }
        for (File sourceFile : sourceFiles) {
            var lines = Files.readString(sourceFile.toPath());
            Program program = parser.produceAST(tokenizer.tokenize(lines));
            var evalRes = List.of((ResourceValue) interpreter.eval(program));

//            System.out.println(evalRes);
            State state = stateStr.isEmpty() ? new State() : mapper.readValue(stateStr, State.class);
            Resource cloud = Resource.builder()
                    .name("main")
                    .type("vm")
                    .build();
//            if (cloudStr.isEmpty()) {
//                cloud = Resource.builder().build();
//            } else {
//                cloud = mapper.readValue(cloudStr, Resource.class);
//            }

            var resources = state.getResources();
            var res = new ArrayList<JsonNode>(resources.size());
            for (var srcResource : evalRes) {
                var src = Resource.builder()
                        .type(srcResource.getSchema())
                        .name(srcResource.getName())
                        .build();
                JsonNode stateResource = resources.stream().filter(it -> it.get("name").equals(src.getName()) && it.get("type").equals(src.getType())).findFirst()
                        .orElseGet(mapper::createObjectNode);
                var it = mapper.readValue(stateResource.toString(), Resource.class);
                var jsonNode = diff.apply(it, src, cloud);
                res.add(jsonNode);
            }
            state.setResources(res);
            mapper.writeValue(this.state, state);

        }
        log.info(stateStr);
    }

    private void createStateIfNotExists() throws IOException {
        if (!state.exists()) {
            if (!Paths.get(DIR).toFile().exists()) {
                Files.createDirectory(Path.of(DIR));
            }
            state = Files.createFile(Path.of(STATE_FILE)).toFile();
        }
    }

    public File[] findByFileName(Path path, String fileName) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.fcl");
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