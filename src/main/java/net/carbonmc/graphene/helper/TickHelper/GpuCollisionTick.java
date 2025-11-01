package net.carbonmc.graphene.helper.TickHelper;

import net.carbonmc.graphene.config.CoolConfig;
import net.carbonmc.graphene.engine.collision.gpu.OpenCLBroadphase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber
public final class GpuCollisionTick {
    private static final Logger LOGGER = LogManager.getLogger("Graphene-GPUCollision");
    private static final OpenCLBroadphase GPU = new OpenCLBroadphase();
    private static final int DEFAULT_RANGE = 64; // blocks around player to sample entities

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post e) {
        if (!Boolean.TRUE.equals(CoolConfig.enableGpuCollision.get())) return;
        MinecraftServer server = e.getServer();
        if (server == null) return;

        for (ServerLevel sl : server.getAllLevels()) {
            // Gather entities near players (unique)
            Set<Entity> set = new HashSet<>();
            for (Player p : sl.players()) {
                BlockPos pos = p.blockPosition();
                AABB range = new AABB(
                        pos.getX() - DEFAULT_RANGE,
                        Math.max(sl.getMinBuildHeight(), pos.getY() - DEFAULT_RANGE),
                        pos.getZ() - DEFAULT_RANGE,
                        pos.getX() + DEFAULT_RANGE,
                        Math.min(sl.getMaxBuildHeight(), pos.getY() + DEFAULT_RANGE),
                        pos.getZ() + DEFAULT_RANGE
                );
                List<LivingEntity> list = sl.getEntitiesOfClass(LivingEntity.class, range);
                set.addAll(list);
            }
            if (set.isEmpty()) continue;

            List<AABB> boxes = new ArrayList<>(set.size());
            int[] ids = new int[set.size()];
            int i = 0;
            for (Entity ent : set) {
                boxes.add(ent.getBoundingBox());
                ids[i++] = ent.getId();
            }

            List<OpenCLBroadphase.Pair> pairs = GPU.computePairs(boxes, ids);
            if (!pairs.isEmpty() && Boolean.TRUE.equals(CoolConfig.DEBUG_LOGGING.get())) {
                LOGGER.debug("GPU AABB pairs @ {}: {} pairs computed (entities: {})", sl.dimension().location(), pairs.size(), boxes.size());
            }
        }
    }
}
