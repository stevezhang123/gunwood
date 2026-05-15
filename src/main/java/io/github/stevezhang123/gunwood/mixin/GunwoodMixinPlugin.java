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
    private static final String FLYWHEEL_BLOCK_ENTITY_STORAGE =
            "dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage";

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
        try {
            Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
