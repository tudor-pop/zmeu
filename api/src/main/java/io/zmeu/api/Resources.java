package io.zmeu.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Resources {

    List<ResourceDeclaration> resources;

}
