package com.kokuxmilsch.celestialaltar.misc;


public enum RitualType {
    SUNNY("sunny"),
    RAIN("rain"),
    THUNDER("thunder"),
    DAY("day"),
    NIGHT("night"),
    EMPTY("empty");

    RitualType(String id) {
        this.id = id;
    }

    public final String id;
}
