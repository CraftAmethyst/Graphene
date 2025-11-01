package net.carbonmc.graphene.client;

import net.carbonmc.graphene.client.gui.ClothConfigScreenFactory;
import net.carbonmc.graphene.config.CoolConfig;
import net.carbonmc.graphene.engine.cull.AABBCullingManager;
import net.carbonmc.graphene.engine.cull.CullCache;
import net.carbonmc.graphene.helper.TickHelper.EntityTickHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GrapheneClient {
    private static final double CAMERA_UPDATE_THRESHOLD_SQ = 1.0;
    public static GrapheneClient instance;
    private final CullCache cache = new CullCache();
    private final AABBCullingManager aabbCulling = new AABBCullingManager();
    private Vec3 lastCameraPos = Vec3.ZERO;
    private int framesSinceLastUpdate = 0;


    public GrapheneClient() {
        instance = this;
    }

    public static void init() {
        NeoForge.EVENT_BUS.register(ItemCountRenderer.class);
        NeoForge.EVENT_BUS.register(EntityTickHelper.class);
        NeoForge.EVENT_BUS.register(ClientEventHandler.class);
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (minecraft, screen) -> ClothConfigScreenFactory.create(screen)
        );
    }

    public static void shutdown() {
        if (instance != null) {
            instance.aabbCulling.dispose();
            instance.cache.clear();
        }
    }

    public boolean shouldSkipEntity(Entity e) {
        if (e == null) return false;
        if (!CoolConfig.isEntityCullingEnabled()) return false;
        if (isEntityBlacklisted(e)) return false;
        updateCameraPosition();
        return aabbCulling.shouldCullEntity(e);
    }

    public boolean shouldSkipBlockEntity(BlockEntity be) {
        if (be == null) return false;
        if (!CoolConfig.isBlockEntityCullingEnabled()) return false;
        updateCameraPosition();
        return aabbCulling.shouldCullBlockEntity(be);
    }

    public CullCache getCullCache() {
        return cache;
    }

    public AABBCullingManager getAABBCullingManager() {
        return aabbCulling;
    }

    private boolean isEntityBlacklisted(Entity entity) {
        ResourceLocation entityId = EntityType.getKey(entity.getType());

        String entityName = entityId.toString();
        List<? extends String> blacklist = CoolConfig.getEntityBlacklist();

        for (String pattern : blacklist) {
            if (matchesPattern(entityName, pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPattern(String entityName, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.endsWith(":*")) {
            String namespace = pattern.substring(0, pattern.length() - 2);
            return entityName.startsWith(namespace + ":");
        }
        return entityName.equals(pattern);
    }

    private void updateCameraPosition() {
        Minecraft mc = Minecraft.getInstance();
        Vec3 currentCameraPos = mc.gameRenderer.getMainCamera().getPosition();
        framesSinceLastUpdate++;
        if (framesSinceLastUpdate >= 5 ||
                currentCameraPos.distanceToSqr(lastCameraPos) > 4.0) {
            lastCameraPos = currentCameraPos;
            aabbCulling.updateCameraPosition();
            framesSinceLastUpdate = 0;
        }
    }
}