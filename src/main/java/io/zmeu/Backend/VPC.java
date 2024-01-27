package io.zmeu.Backend;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
@Setter
public class VPC extends Resource {
    private String cidrBlock;

    public VPC(String cidrBlock) {
        this();
        this.cidrBlock = cidrBlock;
    }

    public VPC(String name, String cidrBlock) {
        super(name);
        this.cidrBlock = cidrBlock;
    }

    public VPC() {
        super();
    }
}
