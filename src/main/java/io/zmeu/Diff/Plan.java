package io.zmeu.Diff;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

public record Plan(@Nullable JsonNode sourceCode,@Nullable JsonNode diffResults) {
}
