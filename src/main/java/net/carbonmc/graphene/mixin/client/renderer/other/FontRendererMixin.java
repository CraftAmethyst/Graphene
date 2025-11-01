package net.carbonmc.graphene.mixin.client.renderer.other;

import net.carbonmc.graphene.util.TBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public class FontRendererMixin {

    @Unique
    private float graphene$endX;

    @Unique
    private TBuilder graphene$underlineBuilder;

    @Unique
    private TBuilder graphene$strikethroughBuilder;

    @Unique
    private float graphene$currentY;

    @Unique
    private Matrix4f graphene$currentMatrix;

    @Unique
    private MultiBufferSource graphene$currentBuffer;

    @Unique
    private int graphene$currentLight;

    public FontRendererMixin(float graphene$endX) {
        this.graphene$endX = graphene$endX;
    }


    @Inject(
            method = "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F",
            at = @At("HEAD")
    )
    private void graphene$onRenderFormattedTextStart(FormattedCharSequence text, float x, float y, int color, boolean shadow, Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode mode, int backgroundColor, int light, CallbackInfoReturnable<Float> cir) {
        this.graphene$currentY = y;
        this.graphene$currentMatrix = matrix;
        this.graphene$currentBuffer = buffer;
        this.graphene$currentLight = light;
        if (this.graphene$underlineBuilder != null) {
            this.graphene$underlineBuilder.reset();
        }
        if (this.graphene$strikethroughBuilder != null) {
            this.graphene$strikethroughBuilder.reset();
        }
    }


    @Inject(
            method = {"renderText*"},
            at = @At("RETURN")
    )
    private void graphene$onRenderTextEnd(CallbackInfoReturnable<Float> cir) {
        if (this.graphene$underlineBuilder != null && this.graphene$underlineBuilder.isBuilding()) {
            this.graphene$underlineBuilder.renderRectangle(
                    this.graphene$currentBuffer,
                    this.graphene$currentMatrix,
                    this.graphene$endX, this.graphene$currentY + 8.0F,
                    this.graphene$currentLight
            );
        }

        if (this.graphene$strikethroughBuilder != null && this.graphene$strikethroughBuilder.isBuilding()) {
            this.graphene$strikethroughBuilder.renderRectangle(
                    this.graphene$currentBuffer,
                    this.graphene$currentMatrix,
                    this.graphene$endX, this.graphene$currentY + 3.5F,
                    this.graphene$currentLight
            );
        }
        this.graphene$underlineBuilder = null;
        this.graphene$strikethroughBuilder = null;
    }
}