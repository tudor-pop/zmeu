package io.zmeu.Diff;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Plan {

    private List<MergeResult> mergeResults = new ArrayList<>();

    public Plan() {
    }
    public Plan(MergeResult... mergeResult) {
        this.mergeResults = new ArrayList<>(Arrays.asList(mergeResult));
    }

    public void add(MergeResult mergeResultByObject) {
        mergeResults.add(mergeResultByObject);
    }
}
