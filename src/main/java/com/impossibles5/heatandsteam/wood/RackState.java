package com.impossibles5.heatandsteam.wood;

import net.minecraft.util.StringRepresentable;

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
