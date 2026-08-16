# Heat & Steam

A Russian sauna for Minecraft. Every sealed room keeps its own temperature, humidity and smoke, raised by a stone stove and shaped by the damper, the door and the water thrown on the stones.

**Minecraft 1.21.1 · NeoForge 21.1.244+ · Java 21 · MIT**

## What it actually is

Most heat in Minecraft belongs to a block: stand near it and something happens. Here it belongs to the *room*. Wall a space in, put a stove in it, and that space starts holding a climate of its own: a temperature that settles where your walls and your fire agree, a humidity you raise a ladle at a time, and smoke that has to go somewhere.

Everything else follows from that. A hall takes longer to heat than a closet and never gets hotter than its walls allow. Glass leaks twice what logs do. Opening the door does not reset the room, it makes it leak. A stove with cold stones warms the air; a stove with a basket of glowing talcochlorite keeps the room steaming for minutes after the fire dies.

## Getting started

1. **Craft a Sauna Stove** (eight stone around an iron ingot) and put it in a room you have sealed properly: walls, a roof, a door. Log walls hold heat best.
2. **Feed it firewood.** Split logs on a Chopping Block, then dry them on a Drying Rack. Damp wood burns cooler and smokes twice as hard.
3. **Load the basket** with sauna stones. River stone is what you start with; talcochlorite from the mountains is what you want.
4. **Hang a Thermometer** on the wall. It reads temperature and humidity live, and its lamp tells you whether the room is actually sealed, which is the one thing you cannot see.
5. **Throw water on the stones** with the ladle once they are hot. That is what makes steam, and it costs the stones real heat.
6. **Sit on the bench and warm through.** Heat rises, so the upper bench is the hotter seat.

Without a chimney the smoke stays with you. That is not a bug, it is the older way of doing this, and it has its own reward.

## Mechanics

### The room

Three values are simulated per sealed room, once a second:

- **Temperature.** Newton's law of cooling in both directions. Heat crosses from the firebox and from the stones down a gradient, and leaks out through the shell. Volume decides how *long* the room takes to come up; wall area and material decide how *hot* it settles.
- **Humidity.** Raised by pouring water on hot stones, lost steadily to condensation. Humid air carries heat far better, so a 60 °C steam room at high humidity beats a 100 °C dry one.
- **Smoke.** A concentration, not an amount. A big room fills slower and clears slower. An open flue or an open door carries it off; a shut damper keeps it in along with the heat.

### The bather

**Warmth** builds while you are in the heat and drains once you leave. It grants regeneration, and near the top it turns dangerous. Danger lives in its own meter, **heat strain**, which builds only near maximum Warmth *and* while you are still in the heat, so stepping out helps immediately. Push it too far and you black out: no damage, but a blackout, heavy slowness and a long wrung-out spell afterwards.

A **Felt Sauna Hat** slows Warmth gain once the room turns dangerous, which is exactly what a real one is for.

### The whisk

Soak a birch or oak whisk in a tub of hot water, then use it in a hot steam room. Whisking someone else is stronger than whisking yourself. Birch gives regeneration and speed, oak gives absorption and resistance.

### Hot to cold

Come out of the heat and straight into cold water or snow and you earn **Hardening**, which sheds freezing and stacks up to three cycles. It also halves your Warmth and flushes heat strain, which is how a session is meant to be paced.

### The smoke sauna

Fire a room with no chimney, or with the damper shut, and the smoke blackens the walls. Fully sooted walls make the steam noticeably softer, and the soot insulates whatever the damper is doing. The price is breathing what the fire makes: **smoke poisoning** bypasses armour, resistance and protection, because nothing you wear helps against carbon monoxide.

### Stove tiers

The tier is *derived* from the masonry you build around the firebox, never assembled from a recipe. Wall the firebox in with Stove Casing for a bigger basket, more capacity and better fuel efficiency. Nothing to disassemble, nothing to go stale: knock a course out and the stove reports a lower tier on its next step.

## Blocks and items

| Blocks | Items |
| --- | --- |
| Sauna Stove, Stove Casing | Sauna Ladle, Felt Sauna Hat |
| Chimney, Damper | Birch and Oak Sauna Whisk |
| Sauna Bench, Water Tub, Sauna Door | Birch, Oak and Spruce Firewood |
| Thermometer | River, Andesite, Basalt and Talcochlorite Stone |
| Chopping Block, Drying Rack | |
| Talcochlorite Ore, Sooty Planks, Sooty Log | |

Talcochlorite ore generates in mountain biomes between y 40 and y 90, in veins of six.

## For pack and mod authors

Everything graded is graded by tag, so you can qualify your own materials without touching the code. Higher tier number is always better.

| Tag | Meaning |
| --- | --- |
| `heat_and_steam:stones/tier_1` … `tier_4` | Sauna stones by heat retention. The tier is the quality the simulation multiplies by: thermal mass is `tier × stoneThermalMassPerQuality`. |
| `heat_and_steam:insulation/tier_1` … `tier_3` | Wall materials by how well they hold heat in. |
| `heat_and_steam:firewood` | What the stove accepts as fuel. |
| `heat_and_steam:cools_stones` | Fluids a scalding stone can be quenched in. Water by default, deliberately not lava. |

The ore is also in `c:ores` and `c:ores/talcochlorite`.

Every tuned number lives in `config/heat_and_steam-common.toml`, grouped into `climate`, `warmth`, `steam`, `smoke`, `stones`, `bath`, `contrast`, `wood` and `compat`. Each option carries a comment explaining what it does and what it trades against.

## Compatibility

**Cold Sweat** (optional). Hardening feeds its freezing point and cold resistance, so a hardened bather genuinely tolerates a colder world. The hook looks its attributes up by id through the vanilla registry, so no Cold Sweat class is ever loaded and its absence cannot throw.

## Building from source

Requires **JDK 21**.

```bash
./gradlew build
```

The jar lands in `build/libs/` as `heat_and_steam-neoforge-1.21.1-<version>.jar`.

Other useful tasks: `./gradlew runData` regenerates every model, blockstate, recipe, loot table, tag and language file into `src/generated/resources`; `./gradlew runClient` and `./gradlew runServer` start a development client and dedicated server.

## Licence

MIT. See [LICENSE](LICENSE). Use it in a modpack, fork it, take the parts you like.
