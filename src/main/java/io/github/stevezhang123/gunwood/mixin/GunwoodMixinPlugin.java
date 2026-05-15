package io.github.stevezhang123.gunwood.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GunwoodMixinPlugin implements IMixinConfigPlugin {
    private static final String FLYWHEEL_BLOCK_ENTITY_STORAGE_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.FlywheelBlockEntityStorageMixin";
    private static final String FLYWHEEL_REBUILD_TASK_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.FlywheelRebuildTaskMixin";
    private static final String FLYWHEEL_VISUAL_MANAGER_IMPL_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.FlywheelVisualManagerImplMixin";
    private static final String CREATE_KINETIC_BLOCK_ENTITY_RENDERER_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.CreateKineticBlockEntityRendererMixin";
    private static final String CREATE_CLIENT_CONTRAPTION_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.CreateClientContraptionMixin";
    private static final String CREATE_CONTRAPTION_VISUAL_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.CreateContraptionVisualMixin";
    private static final String CREATE_CONTRAPTION_ENTITY_RENDERER_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.CreateContraptionEntityRendererMixin";
    private static final String SODIUM_CHUNK_BUILDER_MESHING_TASK_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.sodium.SodiumChunkBuilderMeshingTaskMixin";
    private static final String SODIUM_LEVEL_SLICE_ACCESSOR =
            "io.github.stevezhang123.gunwood.mixin.client.sodium.SodiumLevelSliceAccessor";
    private static final String SODIUM_LIGHT_DATA_ACCESS_MIXIN =
            "io.github.stevezhang123.gunwood.mixin.client.sodium.SodiumLightDataAccessMixin";
    private static final String FLYWHEEL_BLOCK_ENTITY_STORAGE =
            "dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage";
    private static final String FLYWHEEL_VISUAL_MANAGER_IMPL =
            "dev.engine_room.flywheel.impl.visualization.VisualManagerImpl";
    private static final String CREATE_KINETIC_BLOCK_ENTITY_RENDERER =
            "com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer";
    private static final String CREATE_CLIENT_CONTRAPTION =
            "com.simibubi.create.content.contraptions.render.ClientContraption";
    private static final String CREATE_CONTRAPTION_VISUAL =
            "com.simibubi.create.content.contraptions.render.ContraptionVisual";
    private static final String CREATE_CONTRAPTION_ENTITY_RENDERER =
            "com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer";
    private static final String SODIUM_CHUNK_BUILDER_MESHING_TASK =
            "net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask";
    private static final String SODIUM_LIGHT_DATA_ACCESS =
            "net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (FLYWHEEL_BLOCK_ENTITY_STORAGE_MIXIN.equals(mixinClassName)) {
            return isClassPresent(FLYWHEEL_BLOCK_ENTITY_STORAGE);
        }
        if (FLYWHEEL_REBUILD_TASK_MIXIN.equals(mixinClassName)) {
            return isClassPresent(FLYWHEEL_BLOCK_ENTITY_STORAGE);
        }
        if (FLYWHEEL_VISUAL_MANAGER_IMPL_MIXIN.equals(mixinClassName)) {
            return isClassPresent(FLYWHEEL_VISUAL_MANAGER_IMPL);
        }
        if (CREATE_KINETIC_BLOCK_ENTITY_RENDERER_MIXIN.equals(mixinClassName)) {
            return isClassPresent(CREATE_KINETIC_BLOCK_ENTITY_RENDERER);
        }
        if (CREATE_CLIENT_CONTRAPTION_MIXIN.equals(mixinClassName)) {
            return isClassPresent(CREATE_CLIENT_CONTRAPTION);
        }
        if (CREATE_CONTRAPTION_VISUAL_MIXIN.equals(mixinClassName)) {
            return isClassPresent(CREATE_CONTRAPTION_VISUAL);
        }
        if (CREATE_CONTRAPTION_ENTITY_RENDERER_MIXIN.equals(mixinClassName)) {
            return isClassPresent(CREATE_CONTRAPTION_ENTITY_RENDERER);
        }
        if (SODIUM_CHUNK_BUILDER_MESHING_TASK_MIXIN.equals(mixinClassName) || SODIUM_LEVEL_SLICE_ACCESSOR.equals(mixinClassName)) {
            return isClassPresent(SODIUM_CHUNK_BUILDER_MESHING_TASK);
        }
        if (SODIUM_LIGHT_DATA_ACCESS_MIXIN.equals(mixinClassName)) {
            return isClassPresent(SODIUM_LIGHT_DATA_ACCESS);
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isClassPresent(String className) {
        String resourceName = className.replace('.', '/') + ".class";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader != null && classLoader.getResource(resourceName) != null;
    }
}
