package com.kokuxmilsch.celestialaltar.block;

import net.minecraft.util.StringRepresentable;

public enum CrystalParts implements StringRepresentable {
    TOP,
    MIDDLE,
    BOTTOM;

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return switch (this) {
            case TOP -> "top";
            case MIDDLE -> "middle";
            case BOTTOM -> "bottom";
        };
    }
}
