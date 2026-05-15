package io.github.stevezhang123.gunwood.config;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

public final class GunwoodClientConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue PAINTED_BLOCK_OVERLAY_RANGE;
    public static final ModConfigSpec.IntValue MAX_PAINTED_BLOCK_OVERLAY_COUNT;
    public static final ModConfigSpec.BooleanValue SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SCRAPER;
    public static final ModConfigSpec.BooleanValue SHOW_SELECTIONS_WHEN_HOLDING_SCRAPER;
    public static final ModConfigSpec.BooleanValue SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SELECTION_TOOL;
    public static final ModConfigSpec.BooleanValue SHOW_SELECTIONS_WHEN_HOLDING_SELECTION_TOOL;
    public static final ModConfigSpec.BooleanValue SHOW_PAINTED_BLOCKS_WHEN_WEARING_GOGGLES;
    public static final ModConfigSpec.ConfigValue<String> PAINTED_BLOCK_OUTLINE_COLOR;
    public static final ModConfigSpec.ConfigValue<String> SELECTION_OUTLINE_COLOR;
    public static final ModConfigSpec.ConfigValue<String> SELECTION_FACE_HIGHLIGHT_COLOR;
    public static final ModConfigSpec.ConfigValue<String> HOVERED_SELECTION_OUTLINE_COLOR;
    public static final ModConfigSpec.DoubleValue OUTLINE_INFLATION;
    public static final ModConfigSpec.DoubleValue SELECTION_LINE_WIDTH;
    public static final ModConfigSpec.DoubleValue PAINTED_BLOCK_LINE_WIDTH;
    public static final ModConfigSpec.BooleanValue ENABLE_ADVANCED_TOOLTIPS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("overlay");
        PAINTED_BLOCK_OVERLAY_RANGE = builder
                .comment(
                        "手持刮子/选区工具或按配置戴眼镜时，高亮显示已涂漆方块的范围，单位为方块。",
                        "Range in blocks for painted-block overlay when holding a scraper/selection tool or when goggles overlay is enabled.",
                        "默认值：32；范围：4 ~ 128",
                        "Default: 32; Range: 4 ~ 128"
                )
                .defineInRange("paintedBlockOverlayRange", 32, 4, 128);
        MAX_PAINTED_BLOCK_OVERLAY_COUNT = builder
                .comment(
                        "每帧最多渲染多少个已涂漆方块边框。调低可减少大量透明漆方块附近的 FPS 压力。",
                        "Maximum painted-block outlines rendered per frame. Lower this to reduce FPS impact near many painted blocks.",
                        "默认值：2048；范围：0 ~ 65536",
                        "Default: 2048; Range: 0 ~ 65536"
                )
                .defineInRange("maxPaintedBlockOverlayCount", 2048, 0, 65536);
        SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SCRAPER = builder
                .comment(
                        "手持刮子时是否显示附近已涂漆方块。",
                        "Whether nearby painted blocks are highlighted while holding a scraper.",
                        "默认值：true",
                        "Default: true"
                )
                .define("showPaintedBlocksWhenHoldingScraper", true);
        SHOW_SELECTIONS_WHEN_HOLDING_SCRAPER = builder
                .comment(
                        "手持刮子时是否显示当前 Gunwood 选区。",
                        "Whether the active Gunwood selection is shown while holding a scraper.",
                        "默认值：true",
                        "Default: true"
                )
                .define("showSelectionsWhenHoldingScraper", true);
        SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SELECTION_TOOL = builder
                .comment(
                        "手持选区工具时是否显示附近已涂漆方块。",
                        "Whether nearby painted blocks are highlighted while holding the selection tool.",
                        "默认值：true",
                        "Default: true"
                )
                .define("showPaintedBlocksWhenHoldingSelectionTool", true);
        SHOW_SELECTIONS_WHEN_HOLDING_SELECTION_TOOL = builder
                .comment(
                        "手持选区工具时是否显示当前选区和预览框。",
                        "Whether the active selection and preview outline are shown while holding the selection tool.",
                        "默认值：true",
                        "Default: true"
                )
                .define("showSelectionsWhenHoldingSelectionTool", true);
        SHOW_PAINTED_BLOCKS_WHEN_WEARING_GOGGLES = builder
                .comment(
                        "戴 Gunwood 眼镜时是否额外高亮已涂漆方块。默认关闭，避免眼镜视野过于杂乱。",
                        "Whether painted blocks are additionally highlighted while wearing Gunwood glasses. Disabled by default to avoid visual clutter.",
                        "默认值：false",
                        "Default: false"
                )
                .define("showPaintedBlocksWhenWearingGoggles", false);
        builder.pop();

        builder.push("colors");
        PAINTED_BLOCK_OUTLINE_COLOR = builder
                .comment(
                        "已涂漆方块边框颜色，格式为 #RRGGBBAA。解析失败时使用默认蓝绿色。",
                        "Painted block outline color in #RRGGBBAA format. Invalid values fall back to the default cyan-teal.",
                        "默认值：#0DF2D980",
                        "Default: #0DF2D980"
                )
                .define("paintedBlockOutlineColor", "#0DF2D980");
        SELECTION_OUTLINE_COLOR = builder
                .comment(
                        "选区边框颜色，格式为 #RRGGBBAA。应与已涂漆方块颜色明显不同。",
                        "Selection outline color in #RRGGBBAA format. It should be visually distinct from painted-block outlines.",
                        "默认值：#FF9E14FF",
                        "Default: #FF9E14FF"
                )
                .define("selectionOutlineColor", "#FF9E14FF");
        SELECTION_FACE_HIGHLIGHT_COLOR = builder
                .comment(
                        "可调整选区面的高亮颜色，格式为 #RRGGBBAA。",
                        "Highlighted adjustable selection face color in #RRGGBBAA format.",
                        "默认值：#FFF23DFF",
                        "Default: #FFF23DFF"
                )
                .define("selectionFaceHighlightColor", "#FFF23DFF");
        HOVERED_SELECTION_OUTLINE_COLOR = builder
                .comment(
                        "悬停选区的额外亮边颜色，格式为 #RRGGBBAA。",
                        "Extra bright outline color for hovered selections in #RRGGBBAA format.",
                        "默认值：#FFFFFFFF",
                        "Default: #FFFFFFFF"
                )
                .define("hoveredSelectionOutlineColor", "#FFFFFFFF");
        builder.pop();

        builder.push("lines");
        OUTLINE_INFLATION = builder
                .comment(
                        "边框渲染时向外膨胀的距离，用于避免 z-fighting。单位为方块。",
                        "Outward inflation used for outline rendering to avoid z-fighting, in blocks.",
                        "默认值：0.004；范围：0.0 ~ 0.1",
                        "Default: 0.004; Range: 0.0 ~ 0.1"
                )
                .defineInRange("outlineInflation", 0.004D, 0.0D, 0.1D);
        SELECTION_LINE_WIDTH = builder
                .comment(
                        "选区边框线宽倍率。当前 RenderType.lines 后端不支持真正的像素线宽，因此通过重复绘制膨胀边框近似实现。",
                        "Selection outline width multiplier. The current RenderType.lines backend does not support true pixel width, so this is approximated with repeated inflated outlines.",
                        "默认值：2.0；范围：1.0 ~ 6.0",
                        "Default: 2.0; Range: 1.0 ~ 6.0"
                )
                .defineInRange("selectionLineWidth", 2.0D, 1.0D, 6.0D);
        PAINTED_BLOCK_LINE_WIDTH = builder
                .comment(
                        "单个已涂漆方块边框线宽倍率。当前后端通过重复绘制膨胀边框近似实现。",
                        "Painted block outline width multiplier. The current backend approximates this with repeated inflated outlines.",
                        "默认值：1.0；范围：1.0 ~ 6.0",
                        "Default: 1.0; Range: 1.0 ~ 6.0"
                )
                .defineInRange("paintedBlockLineWidth", 1.0D, 1.0D, 6.0D);
        builder.pop();

        builder.push("tooltip");
        ENABLE_ADVANCED_TOOLTIPS = builder
                .comment(
                        "是否启用 Gunwood 物品的 Shift 展开式说明。关闭后不再添加这些额外 tooltip。",
                        "Whether Gunwood items show Shift-expanded descriptions. When disabled, these extra tooltips are not added.",
                        "默认值：true",
                        "Default: true"
                )
                .define("enableAdvancedTooltips", true);
        builder.pop();

        SPEC = builder.build();
    }

    private GunwoodClientConfig() {
    }

    public static Color paintedBlockOutlineColor() {
        return parseColor(PAINTED_BLOCK_OUTLINE_COLOR.get(), new Color(0.05F, 0.95F, 0.85F, 0.5F));
    }

    public static Color selectionOutlineColor() {
        return parseColor(SELECTION_OUTLINE_COLOR.get(), new Color(1.0F, 0.62F, 0.08F, 1.0F));
    }

    public static Color selectionFaceHighlightColor() {
        return parseColor(SELECTION_FACE_HIGHLIGHT_COLOR.get(), new Color(1.0F, 0.95F, 0.25F, 1.0F));
    }

    public static Color hoveredSelectionOutlineColor() {
        return parseColor(HOVERED_SELECTION_OUTLINE_COLOR.get(), new Color(1.0F, 1.0F, 1.0F, 0.75F));
    }

    private static Color parseColor(String value, Color fallback) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }

        if (normalized.length() != 8) {
            LOGGER.warn("Invalid Gunwood color '{}'; expected #RRGGBBAA", value);
            return fallback;
        }

        try {
            int red = Integer.parseInt(normalized.substring(0, 2), 16);
            int green = Integer.parseInt(normalized.substring(2, 4), 16);
            int blue = Integer.parseInt(normalized.substring(4, 6), 16);
            int alpha = Integer.parseInt(normalized.substring(6, 8), 16);
            return new Color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
        } catch (NumberFormatException exception) {
            LOGGER.warn("Invalid Gunwood color '{}'; expected #RRGGBBAA", value);
            return fallback;
        }
    }

    public record Color(float red, float green, float blue, float alpha) {
    }
}
