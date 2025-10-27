package net.carbonmc.graphene.mixin.client.renderer.culling;

import net.carbonmc.graphene.client.GrapheneClient;
import net.carbonmc.graphene.config.CoolConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelTKMixin {

    @Inject(
            method = "guardEntityTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ConEntityTick(Consumer<Entity> consumer, Entity entity, CallbackInfo ci) {
        if (!CoolConfig.isTickStoppingEnabled()) return;
        if (GrapheneClient.instance != null && GrapheneClient.instance.shouldSkipEntity(entity)) {
            ci.cancel();
        }
    }
}