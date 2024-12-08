package io.zmeu.api.resource;

import lombok.Builder;

import java.util.List;

@Builder
public record Resources<T>(List<T> list) {
}
