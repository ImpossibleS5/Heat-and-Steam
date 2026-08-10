package com.banya;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config. Gameplay constants live here (and in the KubeJS layer) rather than as hardcoded
 * Java values — see the design notes. More values (heat rates, warmth thresholds) are added per sub-slice.
 */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_ROOM_VOLUME = BUILDER
            .comment(
                    "Maximum enclosed volume (in blocks) the stove treats as a single parnaya (banya room).",
                    "A space that floods past this cap is considered open/leaking and holds no microclimate.")
            .defineInRange("maxRoomVolume", 512, 8, 32768);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
