package com.impossibles5.heatandsteam.player;

public record WarmthSync(float warmth, float strain, boolean strainRising, boolean inSauna) {
    public static final WarmthSync NONE = new WarmthSync(0.0F, 0.0F, false, false);

    private static final float WARMTH_EPSILON = 0.5F;
    private static final float STRAIN_EPSILON = 0.01F;

    public boolean differsFrom(float otherWarmth, float otherStrain, boolean otherRising, boolean otherInSauna) {
        return this.inSauna != otherInSauna
                || this.strainRising != otherRising
                || moved(this.warmth, otherWarmth, WARMTH_EPSILON)
                || moved(this.strain, otherStrain, STRAIN_EPSILON);
    }

    private static boolean moved(float last, float current, float epsilon) {
        if (last != current && (last <= 0.0F || current <= 0.0F)) {
            return true;
        }
        return Math.abs(last - current) >= epsilon;
    }
}
