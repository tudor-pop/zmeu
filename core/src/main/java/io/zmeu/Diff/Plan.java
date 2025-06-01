package io.zmeu.Diff;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Plan {

    private List<MergeResult> mergeResults = new ArrayList<>();

    public Plan() {
    }

    public void add(MergeResult mergeResultByObject) {
        mergeResults.add(mergeResultByObject);
    }

}
