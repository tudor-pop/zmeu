package io.zmeu.Diff;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Plan {

    private List<DiffResult> diffResults = new ArrayList<>();

    public Plan() {
    }

    public void add(DiffResult diffResultByObject) {
        diffResults.add(diffResultByObject);
    }
}
