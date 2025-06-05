package io.zmeu.Diff;

import io.zmeu.api.resource.Identity;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;

@Slf4j
public class JaversFactory {

    public static Javers createNoDb() {
        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .registerValue(Identity.class, new IdentityComparator())
                .withPrettyPrint(true)
                .build();
    }
}
