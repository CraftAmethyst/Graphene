package net.carbonmc.graphene;

import fuzs.forgeconfigapiport.neoforge.impl.forge.ForgeConfigRegistryImpl;
import net.carbonmc.graphene.client.GrapheneClient;
import net.carbonmc.graphene.command.KillMobsCommand;
import net.carbonmc.graphene.config.CoolConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("graphene")
public class Graphene {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "graphene";
    public static final String VERSION = "2.1.1";
    public static GrapheneClient grapheneClient;

    public Graphene() {
        ForgeConfigRegistryImpl.INSTANCE.register("graphene", ModConfig.Type.COMMON, CoolConfig.SPEC);
        LOGGER.info("Initializing Graphene MOD v{}", VERSION);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            GrapheneClient.init();
            grapheneClient = new GrapheneClient();
        }
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        Runtime.getRuntime().addShutdownHook(new Thread(GrapheneClient::shutdown));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterClientCommandsEvent event) {
        KillMobsCommand.register(event.getDispatcher());
    }
}