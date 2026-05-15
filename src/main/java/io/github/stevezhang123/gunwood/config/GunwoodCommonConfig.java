package io.github.stevezhang123.gunwood.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GunwoodCommonConfig {
    public static final int DEFAULT_SPRAYER_MAX_DAMAGE = 1024;
    public static final int DEFAULT_SCRAPER_MAX_DAMAGE = 1024;

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue SPRAYER_MAX_DAMAGE;
    public static final ModConfigSpec.IntValue SCRAPER_MAX_DAMAGE;
    public static final ModConfigSpec.IntValue SPRAYER_DAMAGE_PER_USE;
    public static final ModConfigSpec.IntValue SCRAPER_DAMAGE_PER_USE;
    public static final ModConfigSpec.BooleanValue USE_UNBREAKING_FOR_SPRAYER_AND_SCRAPER;
    public static final ModConfigSpec.BooleanValue ALLOW_MENDING_ON_SPRAYER_AND_SCRAPER;
    public static final ModConfigSpec.IntValue MAX_SELECTION_VOLUME;
    public static final ModConfigSpec.IntValue MAX_BATCH_OPERATION_BLOCKS;
    public static final ModConfigSpec.BooleanValue ALLOW_SELECTION_BATCH_PAINTING;
    public static final ModConfigSpec.BooleanValue ALLOW_SELECTION_BATCH_SCRAPING;
    public static final ModConfigSpec.BooleanValue ENABLE_FTB_ULTIMINE_COMPAT;
    public static final ModConfigSpec.IntValue MAX_FTB_CHAIN_BLOCKS;
    public static final ModConfigSpec.BooleanValue ENABLE_LIGHT_TRANSPARENCY;
    public static final ModConfigSpec.BooleanValue REFRESH_LIGHT_ON_PAINT_CHANGE;
    public static final ModConfigSpec.BooleanValue ENABLE_SODIUM_COMPAT;
    public static final ModConfigSpec.BooleanValue ENABLE_CREATE_COMPAT;
    public static final ModConfigSpec.BooleanValue ENABLE_CREATE_CONTRAPTION_COMPAT;
    public static final ModConfigSpec.BooleanValue REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("tools");
        SPRAYER_MAX_DAMAGE = builder
                .comment(
                        "喷刷的最大耐久值。修改后可能需要重启游戏或重新进入世界，已有物品的显示才会完全刷新。",
                        "Maximum durability of the spray brush. A restart or world reload may be required for existing items to update visually.",
                        "默认值：1024；范围：1 ~ 65535",
                        "Default: 1024; Range: 1 ~ 65535"
                )
                .defineInRange("sprayerMaxDamage", DEFAULT_SPRAYER_MAX_DAMAGE, 1, 65535);
        SCRAPER_MAX_DAMAGE = builder
                .comment(
                        "刮子的最大耐久值。修改后可能需要重启游戏或重新进入世界，已有物品的显示才会完全刷新。",
                        "Maximum durability of the scraper. A restart or world reload may be required for existing items to update visually.",
                        "默认值：1024；范围：1 ~ 65535",
                        "Default: 1024; Range: 1 ~ 65535"
                )
                .defineInRange("scraperMaxDamage", DEFAULT_SCRAPER_MAX_DAMAGE, 1, 65535);
        SPRAYER_DAMAGE_PER_USE = builder
                .comment(
                        "喷刷每次成功使用消耗的耐久。单方块、FTB 连锁和选区批量都只按一次成功使用计算，不按方块数量计算。0 表示不消耗耐久。",
                        "Durability consumed by each successful spray brush use. Single-block, FTB chain, and selection batch operations count as one use, not per block. 0 disables durability cost.",
                        "默认值：1；范围：0 ~ 1024",
                        "Default: 1; Range: 0 ~ 1024"
                )
                .defineInRange("sprayerDamagePerUse", 1, 0, 1024);
        SCRAPER_DAMAGE_PER_USE = builder
                .comment(
                        "刮子每次成功使用消耗的耐久。单方块、FTB 连锁和选区批量都只按一次成功使用计算，不按方块数量计算。0 表示不消耗耐久。",
                        "Durability consumed by each successful scraper use. Single-block, FTB chain, and selection batch operations count as one use, not per block. 0 disables durability cost.",
                        "默认值：1；范围：0 ~ 1024",
                        "Default: 1; Range: 0 ~ 1024"
                )
                .defineInRange("scraperDamagePerUse", 1, 0, 1024);
        USE_UNBREAKING_FOR_SPRAYER_AND_SCRAPER = builder
                .comment(
                        "喷刷和刮子的耐久消耗是否受耐久附魔影响。关闭后，每次成功使用会直接按配置扣除耐久。",
                        "Whether Unbreaking reduces spray brush and scraper durability loss. When disabled, successful uses directly consume the configured durability.",
                        "默认值：true",
                        "Default: true"
                )
                .define("useUnbreakingForSprayerAndScraper", true);
        ALLOW_MENDING_ON_SPRAYER_AND_SCRAPER = builder
                .comment(
                        "是否允许喷刷和刮子使用经验修补。关闭后，经验修补书不能通过铁砧应用，已有经验修补也不会修复它们。",
                        "Whether spray brushes and scrapers may use Mending. When disabled, Mending books cannot be applied by anvil and existing Mending will not repair them.",
                        "默认值：true",
                        "Default: true"
                )
                .define("allowMendingOnSprayerAndScraper", true);
        builder.pop();

        builder.push("selection");
        MAX_SELECTION_VOLUME = builder
                .comment(
                        "选区工具允许的最大选区体积，单位为方块数。用于避免玩家创建过大的长方体选区。",
                        "Maximum selection volume in blocks. This prevents players from creating excessively large cuboid selections.",
                        "默认值：8192；范围：1 ~ 262144",
                        "Default: 8192; Range: 1 ~ 262144"
                )
                .defineInRange("maxSelectionVolume", 8192, 1, 262144);
        MAX_BATCH_OPERATION_BLOCKS = builder
                .comment(
                        "单次批量涂漆或刮漆最多处理的方块数量。超过上限时只处理前 N 个有效目标，以降低服务器卡顿风险。",
                        "Maximum blocks processed by one batch paint/scrape operation. If exceeded, only the first N valid targets are processed to reduce server lag risk.",
                        "默认值：8192；范围：1 ~ 262144",
                        "Default: 8192; Range: 1 ~ 262144"
                )
                .defineInRange("maxBatchOperationBlocks", 8192, 1, 262144);
        ALLOW_SELECTION_BATCH_PAINTING = builder
                .comment(
                        "是否允许喷刷使用 Gunwood 选区进行批量涂漆。关闭后，有选区时喷刷仍按单方块逻辑工作。",
                        "Whether spray brushes may paint the active Gunwood selection in bulk. When disabled, the brush falls back to single-block behavior.",
                        "默认值：true",
                        "Default: true"
                )
                .define("allowSelectionBatchPainting", true);
        ALLOW_SELECTION_BATCH_SCRAPING = builder
                .comment(
                        "是否允许刮子使用 Gunwood 选区进行批量刮漆。关闭后，有选区时刮子仍按单方块逻辑工作。",
                        "Whether scrapers may scrape the active Gunwood selection in bulk. When disabled, the scraper falls back to single-block behavior.",
                        "默认值：true",
                        "Default: true"
                )
                .define("allowSelectionBatchScraping", true);
        REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS = builder
                .comment(
                        "批量涂漆/刮漆是否检查玩家对每个目标方块的交互权限。推荐服务器保持开启。",
                        "Whether batch paint/scrape operations check player interaction permission for each target block. Recommended enabled on servers.",
                        "默认值：true",
                        "Default: true"
                )
                .define("requireBuildPermissionForBatchOperations", true);
        builder.pop();

        builder.push("compat");
        ENABLE_FTB_ULTIMINE_COMPAT = builder
                .comment(
                        "是否启用 FTB Ultimine 右键连锁涂漆/刮漆兼容。修改后可能需要重启游戏或服务器。",
                        "Whether FTB Ultimine right-click chain paint/scrape compatibility is enabled. A game or server restart may be required.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableFtbUltimineCompat", true);
        MAX_FTB_CHAIN_BLOCKS = builder
                .comment(
                        "单次 FTB 连锁最多处理的方块数量。超过上限时只处理前 N 个有效目标。",
                        "Maximum blocks processed by one FTB chain operation. If exceeded, only the first N valid targets are processed.",
                        "默认值：1024；范围：1 ~ 65536",
                        "Default: 1024; Range: 1 ~ 65536"
                )
                .defineInRange("maxFtbChainBlocks", 1024, 1, 65536);
        ENABLE_SODIUM_COMPAT = builder
                .comment(
                        "是否启用 Sodium 专用隐藏兼容判断。Mixin 加载仍可能需要重启游戏才能完全变化。",
                        "Whether Sodium-specific hiding checks are enabled. Mixin loading changes may still require a game restart.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableSodiumCompat", true);
        ENABLE_CREATE_COMPAT = builder
                .comment(
                        "是否启用 Create/Flywheel 动态渲染隐藏兼容。Mixin 加载仍可能需要重启游戏才能完全变化。",
                        "Whether Create/Flywheel dynamic renderer hiding compatibility is enabled. Mixin loading changes may still require a game restart.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableCreateCompat", true);
        ENABLE_CREATE_CONTRAPTION_COMPAT = builder
                .comment(
                        "是否启用 Create Contraption 动态结构隐藏兼容。Mixin 加载仍可能需要重启游戏才能完全变化。",
                        "Whether Create Contraption moving-structure hiding compatibility is enabled. Mixin loading changes may still require a game restart.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableCreateContraptionCompat", true);
        builder.pop();

        builder.push("light");
        ENABLE_LIGHT_TRANSPARENCY = builder
                .comment(
                        "被透明漆涂过的方块是否允许太阳光和方块光穿透。关闭后只隐藏渲染，不改变光照遮挡。",
                        "Whether painted blocks allow sky light and block light to pass through. When disabled, only rendering is hidden and light occlusion is unchanged.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableLightTransparency", true);
        REFRESH_LIGHT_ON_PAINT_CHANGE = builder
                .comment(
                        "涂漆/刮漆后是否主动刷新光照。关闭后可能需要重新加载区块才会更新光照。",
                        "Whether light is actively refreshed after paint/scrape changes. When disabled, chunk reloads may be required for lighting updates.",
                        "默认值：true",
                        "Default: true"
                )
                .define("refreshLightOnPaintChange", true);
        builder.pop();

        SPEC = builder.build();
    }

    private GunwoodCommonConfig() {
    }

    public static boolean enableLightTransparency() {
        return safeGet(ENABLE_LIGHT_TRANSPARENCY, false);
    }

    public static boolean refreshLightOnPaintChange() {
        return safeGet(REFRESH_LIGHT_ON_PAINT_CHANGE, true);
    }

    public static boolean enableSodiumCompat() {
        return safeGet(ENABLE_SODIUM_COMPAT, true);
    }

    public static boolean enableCreateCompat() {
        return safeGet(ENABLE_CREATE_COMPAT, true);
    }

    public static boolean enableCreateContraptionCompat() {
        return safeGet(ENABLE_CREATE_CONTRAPTION_COMPAT, true);
    }

    private static boolean safeGet(ModConfigSpec.BooleanValue value, boolean fallback) {
        try {
            return value.get();
        } catch (IllegalStateException exception) {
            return fallback;
        }
    }
}
