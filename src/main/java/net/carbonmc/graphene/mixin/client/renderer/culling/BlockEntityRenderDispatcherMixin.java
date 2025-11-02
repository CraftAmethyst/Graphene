package net.carbonmc.graphene.mixin.client.renderer.culling;

import com.mojang.blaze3d.vertex.PoseStack;
import net.carbonmc.graphene.client.GrapheneClient;
import net.carbonmc.graphene.config.CoolConfig;
import net.carbonmc.graphene.engine.cull.CullCache;
import net.carbonmc.graphene.engine.cull.iface.BlockEntityVisibility;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @Shadow
    public abstract <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E blockEntity);

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends BlockEntity> void graphene$earlyCullingCheck(
            E blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffer,
            CallbackInfo ci) {

        if (!CoolConfig.isBlockEntityCullingEnabled()) return;

        GrapheneClient client = GrapheneClient.instance;
        if (client == null || !(blockEntity instanceof BlockEntityVisibility cullable)) return;

        if (client.getCullCache() != null) {
            CullCache.CullResult cached = client.getCullCache().checkBlockEntity(blockEntity);
            if (cached.isCached() && cached.isCulled()) {
                ci.cancel();
                return;
            }
        }

        if (cullable.graphene$isForcedVisible()) return;
        BlockEntityRenderer<E> renderer = getRenderer(blockEntity);
        if (renderer != null && renderer.shouldRenderOffScreen(blockEntity)) return;
        if (client.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer("
                            + "Lnet/minecraft/world/level/block/entity/BlockEntity;"
                            + ")Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private <E extends BlockEntity> void graphene$skipCulledBlockEntity(
            E blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffer,
            CallbackInfo ci) {

        if (!CoolConfig.isBlockEntityCullingEnabled()) return;

        GrapheneClient client = GrapheneClient.instance;
        if (client == null || !(blockEntity instanceof BlockEntityVisibility cullable)) return;

        BlockEntityRenderer<E> renderer = getRenderer(blockEntity);
        if (renderer != null && renderer.shouldRenderOffScreen(blockEntity)) return;

        if (!cullable.graphene$isForcedVisible() && client.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }
}