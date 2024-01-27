package io.zmeu.api;

import lombok.Data;

@Data
public class ResourceDeclaration {
    protected Metadata metadata;
    protected Schema schema;
}
