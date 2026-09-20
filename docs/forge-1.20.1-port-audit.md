# Gunwood 1.20.1 Forge migration audit

This records the migration diagnosis for the `port-1.20.1-forge` branch and the verification performed so far.

| Area | 1.21.1 NeoForge dependency | 1.20.1 Forge treatment |
| --- | --- | --- |
| Build and metadata | NeoGradle, NeoForge dependencies and `neoforge.mods.toml` | ForgeGradle 6, Forge 47.4.18, Java 17 toolchain and `META-INF/mods.toml` |
| Registry and events | `DeferredHolder`, NeoForge buses and client events | Forge `RegistryObject`, `DeferredRegister`, Forge event buses and client distribution guards |
| Networking | custom payload registrars, `StreamCodec` | `SimpleChannel` with explicit message IDs, codecs, directions and enqueued handlers; server owns selection changes |
| Config | NeoForge `ModConfigSpec` | Forge `ForgeConfigSpec`; old config keys and meanings retained, with new Embeddium and water wheel switches |
| Saved data | 1.21.1 `SavedData.Factory` signature | 1.20.1 `computeIfAbsent(load, constructor, name)` and `save(CompoundTag)`; packed long positions remain the on disk form, with legacy compound list loading |
| Items and tooltip | newer durability, enchantment tag and tooltip APIs | 1.20.1 `ItemStack` damage and `getEnchantmentValue`; Forge tooltip events |
| Vanilla rendering | `SectionCompiler` and newer `BlockRenderDispatcher` signatures | `RenderChunk.RebuildTask#compile`, Forge ModelData dispatcher signature and 1.20.1 light hooks |
| Optimized rendering | Sodium 1.21.1 meshing and light internals | conditional Embeddium 1.20.1 `ChunkBuilderMeshingTask`, `WorldSlice` and `LightDataAccess` mixins |
| Create and Flywheel | newer contraption/visual refresh hooks | 1.20.1 renderer, visual manager, rebuild and contraption targets; optional class checks |
| FTB Ultimine | newer right click API | 1.20.1 player data selection API, accessed only when the mod is loaded |
| Curios | newer inventory API | Curios 5.x `LazyOptional.resolve()` and optional reflective slot lookup |
| Resources | singular `recipe`, singular Curios tag directory and new enchantment tag | `recipes`, `data/curios/tags/items`, item recipe results and pack format 15 |

## Water wheel model and operation

Create 1.20.1's standard wheel occupies one block entity position. The large wheel has a center block entity and up to eight `water_wheel_structural` blocks. A structural block's `FACING` points toward the center, sometimes through another structural block. `CreateWaterWheelCompat.resolveWholeWheel` follows at most two links, verifies the center, then collects the wheel's actual 3×3 plane positions. If resolution fails, normal single block painting applies.

Painting and scraping expand each wheel once using packed long deduplication, apply a single tool use, send one batch packet and request batch visual/light refresh. The batch limit preserves the wheel as one logical target, including when the limit is below nine. The existing Create/Flywheel render hooks use painted positions to skip static and dynamic visuals; goggles can restore visibility.

## Behavior and validation limits

- Forge 1.20.1 with Gunwood only: `./gradlew build` succeeds and `runGameTestServer` loads and shuts down on Java 17. The GameTest profile currently registers zero automated gameplay tests.
- The other six requested combinations need in game validation with their matching 1.20.1 mod jars. Client mesh rebuild, dynamic water wheel visuals, Curios equipment, FTB selection and the combined stack have been checked against source APIs, but have not been proven by a live client run.
- The old Sodium config key is retained for configuration compatibility; the 1.20.1 optimized renderer path is controlled by `enableEmbeddiumCompat`.
- The 1.20.1 FTB integration uses its player data selection instead of the 1.21.1 right click event API. Selection results may differ at edge cases.
- A standard Create wheel has one block position, so its whole wheel operation stores one packed position. Its rotating model depends on the dynamic renderer skip path.

The final manual acceptance pass should cover vanilla stone, glasses and Curios glasses, scrape and persistence, Embeddium mesh rebuild, Create shaft/cogwheel and contraptions, both water wheel types from every edge, FTB chain, selection controls, light changes, all optional mod omissions, and a normal dedicated server.
