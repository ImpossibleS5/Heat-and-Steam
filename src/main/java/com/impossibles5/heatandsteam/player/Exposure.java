package com.impossibles5.heatandsteam.player;

public record Exposure(boolean inRoom, double heatIndex, double relativeHeight) {
    public static final Exposure NONE = new Exposure(false, 0.0, 0.0);

    public Exposure merge(double otherHeatIndex, double otherRelativeHeight) {
        if (otherHeatIndex <= this.heatIndex) {
            return new Exposure(true, this.heatIndex, this.relativeHeight);
        }
        return new Exposure(true, otherHeatIndex, otherRelativeHeight);
    }
}
