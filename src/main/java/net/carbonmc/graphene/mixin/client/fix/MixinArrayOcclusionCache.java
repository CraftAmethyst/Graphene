package net.carbonmc.graphene.mixin.client.fix;

import com.logisticscraft.occlusionculling.cache.ArrayOcclusionCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ArrayOcclusionCache.class, remap = false)
public class MixinArrayOcclusionCache {

    @Final
    @Shadow
    private int reachX2;

    @Unique
    private int graphene$clamp(int v) {
        return Math.max(0, Math.min(v, reachX2 - 1));
    }

    @ModifyVariable(
            method = {"setVisible", "setHidden", "getState"},
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private int clampX(int x) {
        return graphene$clamp(x);
    }

    @ModifyVariable(
            method = {"setVisible", "setHidden", "getState"},
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true)
    private int clampY(int y) {
        return graphene$clamp(y);
    }

    @ModifyVariable(
            method = {"setVisible", "setHidden", "getState"},
            at = @At("HEAD"),
            ordinal = 2,
            argsOnly = true)
    private int clampZ(int z) {
        return graphene$clamp(z);
    }
}