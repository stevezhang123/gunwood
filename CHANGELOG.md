# Changelog

## 1.20.1 Forge port — Unreleased

Gunwood is now maintained on two Minecraft version branches:

- `main`: Minecraft 1.21.1 / NeoForge / Java 21
- `port-1.20.1-forge`: Minecraft 1.20.1 / Forge 47.x / Java 17

### Added

- Added an optional Embeddium compatibility path for chunk meshing and light sampling.
- Added whole-water-wheel operations for Create water wheels.
- Added whole-large-water-wheel operations for Create large water wheels and their structural edge blocks.
- Added `enableEmbeddiumCompat`, `enableWholeWaterWheelPainting`, and `enableWholeLargeWaterWheelPainting` common configuration options.
- Added a Forge 1.20.1 migration audit at `docs/forge-1.20.1-port-audit.md`.

### Changed

- Ported the project from NeoForge 1.21.1 to Forge 1.20.1 on this branch.
- Moved the build to ForgeGradle, Forge 47.x, Java 17, and Forge `mods.toml` metadata.
- Replaced NeoForge payload registration and `StreamCodec` networking with Forge `SimpleChannel` messages.
- Updated registry, event bus, client event, saved-data, item durability, enchantment, recipe, tag, and pack metadata APIs for 1.20.1.
- Updated vanilla chunk rebuild and renderer mixins for 1.20.1.
- Updated Create, Flywheel, Contraption, FTB Ultimine, and Curios compatibility layers for their 1.20.1 APIs.
- Renamed the optimized-renderer mixin package from Sodium to Embeddium. The legacy `enableSodiumCompat` config key remains for compatibility, while the 1.20.1 renderer path uses `enableEmbeddiumCompat`.

### Fixed

- Large water wheel operations now resolve from any structural edge block to the wheel center.
- Painting and scraping a large water wheel updates all resolved wheel positions in one operation, consumes one tool use, and sends one batch sync.
- Batch limits now preserve a water wheel as one logical target and do not leave a large wheel partially processed.

### Compatibility

- Embeddium, Create, Flywheel, FTB Ultimine, and Curios remain optional. Gunwood can start without any of them installed.
- Standard water wheels use one stored block position because Create implements them as one block entity; their dynamic model is hidden through the Create/Flywheel render hooks.
- Large water wheels store the center and resolved structural positions, allowing edge painting and symmetric whole-wheel scraping.

### Verification

- Built successfully with Java 17 and Forge 47.4.18 using `./gradlew build`.
- Started the Forge GameTest dedicated server with Gunwood only; it loaded and shut down normally.
- Live client validation is still required for Embeddium, Create/Flywheel, FTB Ultimine, Curios, and the full optional-mod stack.

### Upgrade notes

- This branch targets Minecraft 1.20.1 Forge and cannot be installed alongside the 1.21.1 NeoForge build.
- Build artifacts should be named by Minecraft version and loader when released, for example `gunwood-1.20.1-forge-<version>.jar` and `gunwood-1.21.1-neoforge-<version>.jar`.

## Previous 1.21.1 NeoForge changes

This update focuses on post-release stability, performance, and compatibility fixes for Gunwood's painted/hidden block system.

### Added

- Added optional compatibility support for StructureTemplate-based schematics, including Sable schematic workflows.
- Gunwood painted/hidden block metadata is now saved into schematics as relative packed block positions.
- Schematic placement restores Gunwood painted/hidden state at the target location, including vanilla rotation and mirror transforms.
- Added Sable-related common config options:
  - `enableSableCompat`
  - `enableSableSchematicCompat`
  - `skipLightRefreshInSablePhysicalBodies`

### Changed

- Migrated painted/hidden block storage from `Set<BlockPos>` to long-based storage using `BlockPos.asLong()` and fastutil primitive collections.
- Client painted block cache now uses `LongOpenHashSet` for high-frequency render, light, Sodium, and Create/Flywheel visibility checks.
- Server saved data now stores painted block positions internally as packed longs.
- Network sync payloads now send packed long positions instead of serialized `BlockPos` lists.
- Saved data is now written as a long array for lower overhead and smaller serialized data.

### Fixed

- Fixed high CPU overhead caused by `BlockPos` hash collisions in large painted block caches.
- Fixed light refresh crashes when Gunwood interacts with Sable physical bodies or invalid/virtual plot holders.
- Light refresh now checks world bounds and loaded chunks before touching block update, sky light, or light engine APIs.
- Failed light refreshes in unsafe virtual holders are skipped safely instead of crashing the game.
- Old Gunwood saved data using the previous compound-list position format is still readable and migrates on next save.

### Compatibility

- Existing sprayer, scraper, glasses, selection tool, FTB Ultimine, Sodium, Create/Flywheel, Create Contraption, Curios, and light transparency behavior should continue to work.
- Gunwood does not hard-depend on Sable; the mod still starts normally when Sable or Sable Schematic API is not installed.
- Schematic compatibility is implemented through Minecraft structure template data, so tools that bypass `StructureTemplate` may still need dedicated integration later.

### Testing

- Built successfully with `./gradlew build`.
- Recommended manual checks:
  - large painted caches and client FPS/profiling;
  - old save migration and new save persistence;
  - multiplayer painted block sync;
  - normal paint/scrape light refresh;
  - unloaded chunk safety;
  - Sable physical body painting;
  - Sable schematic save/load with rotation and mirror;
  - startup without optional compat mods.
