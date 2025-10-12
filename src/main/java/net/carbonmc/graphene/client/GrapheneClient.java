package net.carbonmc.graphene.client;

import net.carbonmc.graphene.client.gui.ClothConfigScreenFactory;
import net.carbonmc.graphene.engine.cull.AsyncTracer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class GrapheneClient {
    public static void init() {
        GLFW.glfwSwapInterval(0);
        MinecraftForge.EVENT_BUS.register(ItemCountRenderer.class);
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (mc, parent) -> ClothConfigScreenFactory.create(parent)
                    )
            );
    }
    public static void stop(){
        AsyncTracer.shutdown();
    }
}