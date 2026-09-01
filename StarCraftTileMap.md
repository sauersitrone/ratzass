
StarCraftFrameTile.java — libtiled-backed replacement for `StarCraftFrame`.

**libtiled model structure:**

| Layer | Type | Purpose |
|---|---|---|
| `terrain` | `TileLayer` | 4 colour-coded `Tile` variants (dark/mid/light/creep), colour stored as `"color"` property |
| `regions` | `ObjectGroup` | One 
| `terrain` | `TileLayer` | 4 `Tile` variants (dark / mid / light / creep), colour stored as `"color"` property |
| `regions` | `ObjectGroup` | One `MapObject` rectangle per BWAPI `Region` |
| `chokepoints` | `ObjectGroup` | One `MapObject` ellipse per BWEM `ChokePoint` |
| `starting_locations` | `ObjectGroup` | 4×3-tile rectangles at each start spot |
| `minerals` / `geysers` | `ObjectGroup` | Per-unit resource rectangles |
| `enemy_units` / `ally_units` / `neutral_units` | `ObjectGroup` | Per-unit rectangles; `MapObject.name` carries the unit ID for label rendering |

**Key design decisions**
- `initTiledMap()` runs lazily on the first timer tick, after BWAPI has initialised the map.
- `updateTiledMap()` is called every 200 ms before `repaint()` — it rebuilds dynamic groups from live game state and rewrites the terrain layer only when `IgnoreBases` is false.
- Rendering reads purely from the libtiled model: `paintTerrainLayer` walks the `TileLayer`, while `paintObjectGroupFilled/Outline/Ellipses` walk their respective `ObjectGroup` iterators.
- The influence map bypasses the libtiled model (no static tile representation makes sense for a per-frame heat map).
- `Command.order` is a `UnitCommandType` enum — `isPositionBased`/`isTargetUnitBased` replace the original `switch` block.

3 Aufgaben erstellt