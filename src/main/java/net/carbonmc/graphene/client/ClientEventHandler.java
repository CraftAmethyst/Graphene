package net.carbonmc.graphene.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide() && GrapheneClient.instance != null) {
            GrapheneClient.instance.getAABBCullingManager().dispose();
        }
    }
}