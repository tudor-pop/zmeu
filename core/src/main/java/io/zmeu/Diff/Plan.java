package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class Plan {

    private List<MergeResult> mergeResults = new ArrayList<>();

    public Plan() {
    }

    public void add(MergeResult mergeResultByObject) {
        mergeResults.add(mergeResultByObject);
    }

    public Optional<Resource> findResourceByName(String name) {
        return mergeResults.stream()
                .map(MergeResult::resource)
                .filter(Objects::nonNull)
                .filter(it -> StringUtils.equals(it.getResourceName(), name))
                .findFirst();
    }
}
