package net.carbonmc.graphene.mixin.client.renderer.culling;

import net.carbonmc.graphene.helper.TickHelper.EntityTickHelper;
import net.carbonmc.graphene.client.GrapheneClient;
import net.carbonmc.graphene.config.CoolConfig;
import net.carbonmc.graphene.engine.cull.CullCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(
            method = "shouldRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends Entity> void graphene$earlyCullingCheck(
            E entity, Frustum frustum, double camX, double camY, double camZ,
            CallbackInfoReturnable<Boolean> cir) {

        if (!CoolConfig.ite.get()) return;

        GrapheneClient client = GrapheneClient.instance;
        if (client == null) return;
        if (client.getCullCache() != null) {
            CullCache.CullResult cached = client.getCullCache().checkEntity(entity);
            if (cached.isCached() && cached.isCulled()) {
                cir.setReturnValue(false);
                return;
            }
        }
        if (EntityTickHelper.shouldSkipTick(entity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "shouldRender",
            at = @At("TAIL"),
            cancellable = true
    )
    private <E extends Entity> void graphene$skipCulledOrTickSkippedEntity(
            E entity, Frustum frustum, double camX, double camY, double camZ,
            CallbackInfoReturnable<Boolean> cir) {
        if (!CoolConfig.ite.get()) return;
        if (!cir.getReturnValue()) return;

        if (EntityTickHelper.shouldSkipTick(entity) ||
                (GrapheneClient.instance != null && GrapheneClient.instance.shouldSkipEntity(entity))) {
            cir.setReturnValue(false);
        }
    }
}