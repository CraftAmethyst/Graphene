package net.carbonmc.graphene.client;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide() && GrapheneClient.instance != null) {
            GrapheneClient.instance.getAABBCullingManager().dispose();
        }
    }
}