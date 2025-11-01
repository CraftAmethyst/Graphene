package net.carbonmc.graphene.mixin.client.renderer.particle;

import net.carbonmc.graphene.config.CoolConfig;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Particle.class)
public abstract class MixinParticle {
    @Shadow
    protected double xd;
    @Shadow
    protected double yd;
    @Shadow
    protected double zd;
    @Shadow
    protected float alpha;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/Particle;move(DDD)V"
            )
    )
    private void redirectMove(Particle instance, double x, double y, double z) {
        if (CoolConfig.ENABLE_PARTICLE_OPTIMIZATION.get() &&
                CoolConfig.ENABLE_FIXED_TIMESTEP.get()) {
            float timestep = CoolConfig.FIXED_TIMESTEP_INTERVAL.get().floatValue();
            instance.move(xd * timestep, yd * timestep, zd * timestep);
        } else {
            instance.move(x, y, z);
        }
    }
}