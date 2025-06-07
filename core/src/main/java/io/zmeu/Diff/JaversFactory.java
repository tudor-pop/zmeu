package io.zmeu.Diff;

import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;

@Slf4j
public class JaversFactory {

    public static Javers createNoDb() {
        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .withPrettyPrint(true)
                .build();
    }
}
