package io.zmeu.api;

import lombok.Builder;

import java.util.List;

@Builder
public record Resources(List<Resource> list) {
}
