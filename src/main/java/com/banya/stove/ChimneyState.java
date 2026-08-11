package com.banya.stove;

/** What sits above the stove, which decides where the smoke goes and how much heat goes with it. */
public enum ChimneyState {
    /** No flue at all: the smoke stays in the room. This is the banya po-chornomu. */
    NONE,
    /** Flue clear and the damper open: smoke leaves, and so does a good deal of heat. */
    OPEN,
    /** Damper shut: the heat stays in — and so does any smoke the fire is still making. */
    CLOSED
}
