package org.simbasecurity.core.domain;

public class RoleTestBuilder {
    private String name;

    public static RoleTestBuilder role(){
        return new RoleTestBuilder();
    }

    private RoleTestBuilder() {

    }

    public Role build(){
        return new RoleEntity(name);
    }

    public RoleTestBuilder name(String name) {
        this.name = name;
        return this;
    }
}