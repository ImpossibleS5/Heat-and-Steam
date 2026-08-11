package com.banya.wood;

import net.minecraft.util.StringRepresentable;

/** What a drying rack looks like from outside: bare, loaded with damp wood, or ready to burn. */
public enum RackState implements StringRepresentable {
    EMPTY("empty"),
    DRYING("drying"),
    DRY("dry");

    private final String name;

    RackState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
