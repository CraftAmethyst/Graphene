package net.carbonmc.graphene.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TBuilder {
    private static final ResourceLocation FONT_TEXTURE = new ResourceLocation("textures/font/ascii.png");
    private final float startX;
    private final float startY;
    private final float zIndex;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    public boolean building;

    public TBuilder(float startX, float startY, float red, float green, float blue, float alpha, float zIndex) {
        this.alpha = alpha;
        this.blue = blue;
        this.green = green;
        this.red = red;
        this.startX = startX;
        this.startY = startY;
        this.zIndex = zIndex;
        this.building = true;
    }

    public void renderRectangle(MultiBufferSource bufferSource, Matrix4f matrix, float endX, float endY, int light) {
        if (!this.building) return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.textIntensitySeeThrough(FONT_TEXTURE));
        vertexConsumer.vertex(matrix, this.startX, this.startY, this.zIndex)
                .color(this.red, this.green, this.blue, this.alpha)
                .uv2(light)
                .endVertex();

        vertexConsumer.vertex(matrix, endX, this.startY, this.zIndex)
                .color(this.red, this.green, this.blue, this.alpha)
                .uv2(light)
                .endVertex();

        vertexConsumer.vertex(matrix, endX, endY, this.zIndex)
                .color(this.red, this.green, this.blue, this.alpha)
                .uv2(light)
                .endVertex();

        vertexConsumer.vertex(matrix, this.startX, endY, this.zIndex)
                .color(this.red, this.green, this.blue, this.alpha)
                .uv2(light)
                .endVertex();

        this.building = false;
    }

    public boolean isBuilding() {
        return this.building;
    }

    public void reset() {
        this.building = false;
    }
}