package dev.fangscl.Diff;

import dev.fangscl.Backend.VPC;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Log4j2
class DiffTest {
    private Diff diff;
    private final Printer printer = new Printer();

    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    }

    @Test
    void noChanges() {
        var localState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();

        var sourceState = VPC.builder().name("main")
                .cidrBlock("local")
                .build();

        var cloudState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();
        printer.print(res);
        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void sourceChangeOverridesRemote() {
        var localState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();

        var sourceState = VPC.builder().name("main")
                .cidrBlock("src")
                .build();

        var cloudState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addClusterToRemote() {
        var localState = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();

        var sourceState = VPC.builder().name("main")
                .cidrBlock("src")
                .build();

        var res = diff.plan(localState, sourceState, null);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addClusterToLocal() {
        var sourceState = VPC.builder().name("main")
                .cidrBlock("src")
                .build();
        var remoteState = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        var res = diff.plan(null, sourceState, remoteState);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
    void addClusterToLocalAndRemote() {
        var sourceState = VPC.builder().name("main")
                .cidrBlock("src")
                .build();

        var res = diff.plan(null, sourceState, null);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @SneakyThrows
    @Test
    public void shouldCompareTwoEntities() {
        var localState = VPC.builder()
                .id("main")
                .name("main")
                .cidrBlock("src")
                .build();

        var sourceState = VPC.builder()
                .id("main")
                .name("main")
                .build();

        var remoteState = VPC.builder()
                .id("main")
                .name("main")
                .cidrBlock("remote")
                .build();
//        var commit = javers.commit("tudor", remoteState);
        var res = this.diff.plan(localState, sourceState, remoteState);

        //then
        Assertions.assertEquals(res.sourceCode(), this.diff.getMapper().valueToTree(sourceState));
    }

    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeClusterPropertiesFromSrc() {
        var localState = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        var srcState = VPC.builder()
                .name("main")
                .build();
        var remoteState = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        var res = diff.plan(localState, srcState, remoteState);
        var plan = VPC.builder()
                .name("main")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();

        var sourceState = VPC.builder().name("main")
                .cidrBlock("src")
                .build();

        var cloudState = VPC.builder()
                .name("main")
                .cidrBlock("local")
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = VPC.builder()
                .name("main")
                .cidrBlock("src")
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

}