package net.carbonmc.graphene;

import net.carbonmc.graphene.client.GrapheneClient;
import net.carbonmc.graphene.command.KillMobsCommand;
import net.carbonmc.graphene.config.CoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;

@Mod("graphene")
public class Graphene {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "graphene";
    public static final String VERSION = "2.1.3";
    public static GrapheneClient grapheneClient;

    public Graphene() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        var forgeEventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoolConfig.SPEC);
        forgeEventBus.register(this);
        MixinBootstrap.init();
        modEventBus.addListener(this::onClientStop);
        LOGGER.info("Initializing Graphene MOD v{}", VERSION);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GrapheneClient::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            grapheneClient = new GrapheneClient();
        });
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true)
        );
    }

    public void onClientStop(FMLClientSetupEvent event) {
        Runtime.getRuntime().addShutdownHook(new Thread(GrapheneClient::shutdown));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        KillMobsCommand.register(event.getDispatcher());
    }
}