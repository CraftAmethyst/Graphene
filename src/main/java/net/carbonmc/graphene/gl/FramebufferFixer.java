package net.carbonmc.graphene.gl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface FramebufferFixer {
    void graphene$cleanup();

    default void close() {
        graphene$cleanup();
    }
}