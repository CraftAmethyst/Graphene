package net.carbonmc.graphene.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoolConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLE_PARTICLE_OPTIMIZATION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PARTICLE_LOD;
    public static final ForgeConfigSpec.DoubleValue LOD_DISTANCE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue LOD_REDUCTION_FACTOR;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FIXED_TIMESTEP;
    public static final ForgeConfigSpec.DoubleValue FIXED_TIMESTEP_INTERVAL;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOD_PARTICLE_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOD_PARTICLE_BLACKLIST;
    public static final ForgeConfigSpec.BooleanValue fpsoo;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FIXED_LIGHT;
    public static final ForgeConfigSpec.BooleanValue enableReflex;
    public static final ForgeConfigSpec.LongValue reflexOffsetNs;
    public static final ForgeConfigSpec.BooleanValue reflexDebug;
    public static final ForgeConfigSpec.IntValue MAX_FPS;
    public static final ForgeConfigSpec.BooleanValue skipOutlineWhenNoGlowing;
    public static final ForgeConfigSpec.BooleanValue FIX_PEARL_LEAK;
    public static final ForgeConfigSpec.BooleanValue FIX_PROJECTILE_LERP;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue MAX_STACK_SIZE;
    public static final ForgeConfigSpec.BooleanValue MemoryLeakFix_AE2WTLibCreativeTabLeakFix;
    public static final ForgeConfigSpec.BooleanValue MemoryLeakFix_ScreenshotByteBufferLeakFix;
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;
    public static final ForgeConfigSpec.BooleanValue BambooLight;
    public static final ForgeConfigSpec.BooleanValue REDUCE_FPS_WHEN_INACTIVE;
    public static final ForgeConfigSpec.IntValue INACTIVE_FPS_LIMIT;
    public static final ForgeConfigSpec.BooleanValue REDUCE_RENDER_DISTANCE_WHEN_INACTIVE;
    public static final ForgeConfigSpec.IntValue INACTIVE_RENDER_DISTANCE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_OPTIMIZATION;
    public static final ForgeConfigSpec.EnumValue<RenderMode> RENDER_MODE;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_ENDER_CHESTS;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_TRAPPED_CHESTS;
    public static final ForgeConfigSpec.IntValue MAX_RENDER_DISTANCE;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_ENTITY_CLEANUP;
    // GPU Collision
    public static ForgeConfigSpec.BooleanValue enableGpuCollision;
    public static ForgeConfigSpec.IntValue gpuCollisionMaxPairs;
    public static ForgeConfigSpec.BooleanValue enableleafCulling;
    public static ForgeConfigSpec.BooleanValue enableCulling;
    public static ForgeConfigSpec.BooleanValue enableEntityCulling;
    public static ForgeConfigSpec.BooleanValue enableBlockEntityCulling;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> entityBlacklist;
    public static ForgeConfigSpec.BooleanValue enableTickStopping;
    public static ForgeConfigSpec.BooleanValue enableNameTagCulling;
    public static ForgeConfigSpec.BooleanValue optimizeEntities;
    public static ForgeConfigSpec.BooleanValue ite;
    public static ForgeConfigSpec.IntValue horizontalRange;
    public static ForgeConfigSpec.IntValue verticalRange;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> entityWhitelist;
    public static ForgeConfigSpec.BooleanValue tickRaidersInRaid;
    public static ForgeConfigSpec.BooleanValue OpenIO;
    public static ForgeConfigSpec.IntValue maxStackSize;
    public static ForgeConfigSpec.DoubleValue mergeDistance;
    public static ForgeConfigSpec.BooleanValue lockMaxedStacks;
    public static ForgeConfigSpec.IntValue listMode;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemList;
    public static ForgeConfigSpec.BooleanValue showStackCount;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemWhitelist;
    public static ForgeConfigSpec.BooleanValue NoOpenGLError;
    public static ForgeConfigSpec SPEC;

    static {
        BUILDER.push("减少不必要的日志 | NoLog");
        NoOpenGLError = BUILDER
                .comment("取消OpenGL错误日志")
                .define("disable opengl error log", true);
        BUILDER.pop();

        BUILDER.push("Light");
        ENABLE_FIXED_LIGHT = BUILDER
                .comment("光照优化")
                .define("enableFixedLight", true);
        BambooLight = BUILDER
                .comment("竹子光照优化")
                .define("enablebambooFixedLight", true);
        BUILDER.pop();

        BUILDER.push("Reflex");
        enableReflex = BUILDER
                .comment("启用类似 NVIDIA Reflex 的动态低延迟调度")
                .define("enableReflex", true);
        reflexOffsetNs = BUILDER
                .comment("Reflex 微调等待时间（纳秒）。",
                        "GPU 吃不满就加正数；队列堆积就加负数。")
                .defineInRange("reflexOffsetNs", 0L, -1_000_000L, 1_000_000L);
        MAX_FPS = BUILDER
                .comment("Hard framerate cap (0 = disable)")
                .defineInRange("maxFps", 0, 0, 1000);
        reflexDebug = BUILDER
                .comment("在日志中输出每帧等待时间，方便调试")
                .define("reflexDebug", false);
        BUILDER.pop();

        BUILDER.push("粒子优化 | particle Optimization");
        ENABLE_PARTICLE_OPTIMIZATION = BUILDER.comment(
                        "启用粒子系统优化",
                        "Enable particle system optimizations")
                .define("enableParticleOptimization", true);

        ENABLE_PARTICLE_LOD = BUILDER.comment(
                        "启用粒子LOD系统 (Level of Detail)",
                        "Enable particle LOD system (Level of Detail)")
                .define("enableParticleLOD", true);

        LOD_DISTANCE_THRESHOLD = BUILDER.comment(
                        "LOD距离阈值 (方块)",
                        "Distance threshold for LOD reduction (blocks)")
                .defineInRange("lodDistanceThreshold", 16.0, 4.0, 64.0);

        LOD_REDUCTION_FACTOR = BUILDER.comment(
                        "LOD减少因子 (0.0-1.0)",
                        "Reduction factor for LOD (0.0-1.0)")
                .defineInRange("lodReductionFactor", 0.3, 0.0, 1.0);

        ENABLE_FIXED_TIMESTEP = BUILDER.comment(
                        "启用固定时间步长",
                        "Enable fixed timestep for particle physics")
                .define("enableFixedTimestep", false);

        FIXED_TIMESTEP_INTERVAL = BUILDER.comment(
                        "固定时间步长间隔 (秒)",
                        "Fixed timestep interval in seconds")
                .defineInRange("fixedTimestepInterval", 0.05, 0.001, 0.1);

        LOD_PARTICLE_WHITELIST = BUILDER.comment(
                        "始终应用LOD的粒子类型 (即使不在低优先级列表)",
                        "particle types that always use LOD (even iface not low priority)")
                .defineList("lodParticleWhitelist",
                        List.of("minecraft:rain", "minecraft:smoke"),
                        o -> o instanceof String);

        LOD_PARTICLE_BLACKLIST = BUILDER.comment(
                        "从不应用LOD的粒子类型",
                        "particle types that never use LOD")
                .defineList("lodParticleBlacklist",
                        List.of("minecraft:portal", "minecraft:enchant"),
                        o -> o instanceof String);

        BUILDER.pop();

        BUILDER.comment("树叶剔除")
                .push("leaf culling");
        enableleafCulling = BUILDER
                .comment("enableleafculling")
                .define("enableCulling", true);

        BUILDER.comment("实体剔除")
                .push("culling");
        enableCulling = BUILDER
                .comment("Master switch for entity culling functionality. If disabled, all culling features are turned off.")
                .define("enableCulling", true);
        enableEntityCulling = BUILDER
                .comment("Enable entity culling")
                .define("enableEntityCulling", true);
        enableBlockEntityCulling = BUILDER
                .comment("Enable shapes entity culling")
                .define("enableBlockEntityCulling", true);
        entityBlacklist = BUILDER
                .comment("Entity blacklist using namespace patterns (e.g. 'minecraft:*' to blacklist all vanilla entities)")
                .defineList("entityBlacklist",
                        Arrays.asList("minecraft:player", "minecraft:villager"),
                        obj -> obj instanceof String);
        enableTickStopping = BUILDER
                .comment("Stop ticking for culled entities")
                .define("enableTickStopping", false);
        enableNameTagCulling = BUILDER
                .comment("Cull name tags for hidden entities")
                .define("enableNameTagCulling", true);
        BUILDER.pop();

        BUILDER.push("高版本mc优化移植");
        FIX_PEARL_LEAK = BUILDER.define("fixPearlChunkLeak", true);
        FIX_PROJECTILE_LERP = BUILDER.define("fixProjectileInterpolation", true);
        BUILDER.pop();

        BUILDER.push("渲染优化 | Rendering Optimization");
        skipOutlineWhenNoGlowing = BUILDER
                .comment("Skip outline rendering when no glowing entities are in view")
                .define("skipOutlineWhenNoGlowing", true);
        fpsoo = BUILDER
                .comment("减少渲染延迟")
                .define("fpsoo", true);
        BUILDER.pop();

        BUILDER.push("chest_optimization");
        ENABLE_OPTIMIZATION = BUILDER
                .comment("Enable chest rendering optimization")
                .define("enableOptimization", true);

        RENDER_MODE = BUILDER
                .comment("Rendering mode")
                .defineEnum("renderMode", RenderMode.SIMPLE);

        OPTIMIZE_ENDER_CHESTS = BUILDER
                .comment("Optimize ender chests")
                .define("optimizeEnderChests", true);

        OPTIMIZE_TRAPPED_CHESTS = BUILDER
                .comment("Optimize trapped chests")
                .define("optimizeTrappedChests", false);

        MAX_RENDER_DISTANCE = BUILDER
                .comment("Max render distance in chunks")
                .defineInRange("maxRenderDistance", 32, 1, 128);
        BUILDER.pop();

        BUILDER.push("非活动状态优化 | Inactive Optimization");
        REDUCE_FPS_WHEN_INACTIVE = BUILDER.comment(
                        "窗口非活动时降低FPS",
                        "Enable FPS reduction when window is inactive")
                .define("reduceFpsWhenInactive", false);
        INACTIVE_FPS_LIMIT = BUILDER.comment(
                        "非活动状态FPS限制 (5-60)",
                        "FPS limit when window is inactive (5-60)")
                .defineInRange("inactiveFpsLimit", 10, 5, 60);
        REDUCE_RENDER_DISTANCE_WHEN_INACTIVE = BUILDER.comment(
                        "窗口非活动时降低渲染距离",
                        "Enable render distance reduction when window is inactive")
                .define("reduceRenderDistanceWhenInactive", false);
        INACTIVE_RENDER_DISTANCE = BUILDER.comment(
                        "非活动状态渲染距离 (2-12)",
                        "Render distance when window is inactive (2-12)")
                .defineInRange("inactiveRenderDistance", 2, 2, 12);
        BUILDER.pop();

        // GPU Collision section
        BUILDER.push("GPU 碰撞 | GPU Collision");
        enableGpuCollision = BUILDER
                .comment("在服务端使用 OpenCL 将实体 AABB broad-phase 碰撞计算下放到 GPU (需要安装厂商 OpenCL 驱动)")
                .define("enableGpuCollision", false);
        gpuCollisionMaxPairs = BUILDER
                .comment("GPU broad-phase 输出候选对上限，超过将回退/分块重算")
                .defineInRange("gpuCollisionMaxPairs", 65536, 1024, 10_000_000);
        BUILDER.pop();

        BUILDER.comment("实体优化 | Entity Optimization").push("entity_optimization");
        BUILDER.push("实体Tick优化 | Entity Tick Optimization");
        optimizeEntities = BUILDER.comment(
                        "启用实体tick优化",
                        "Enable entity tick optimization")
                .define("optimizeEntities", true);

        horizontalRange = BUILDER.comment(
                        "水平检测范围(方块)",
                        "Horizontal detection range (blocks)")
                .defineInRange("horizontalRange", 64, 1, 256);
        verticalRange = BUILDER.comment(
                        "垂直检测范围(方块)",
                        "Vertical detection range (blocks)")
                .defineInRange("verticalRange", 32, 1, 256);
        ite = BUILDER
                .comment("停止tick实体取消渲染")
                .define("ite", true);
        BUILDER.pop();

        BUILDER.push("实体白名单 | Entity Whitelist");
        OPTIMIZE_ENTITY_CLEANUP = BUILDER.comment(
                        "启用死亡实体清理",
                        "Enable dead entity cleanup")
                .define("entityCleanup", true);
        entityWhitelist = BUILDER.comment(
                        "实体白名单（始终不优化）",
                        "Entity whitelist (always optimized)")
                .defineList("entityWhitelist", List.of("minecraft:ender_dragon"), o -> true);
        BUILDER.pop();

        BUILDER.push("袭击事件 | Raid Events");
        tickRaidersInRaid = BUILDER.comment(
                        "在袭击中保持袭击者tick",
                        "Keep raider ticking during raids")
                .define("tickRaidersInRaid", true);
        BUILDER.pop();

        BUILDER.pop();
        BUILDER.comment("物品优化 | Item Optimization").push("item_optimization");

        OpenIO = BUILDER.comment(
                        "启用物品优化系统",
                        "Enable item optimization system")
                .define("OpenIO", true);

        BUILDER.push("堆叠合并 | Stack Merging");
        maxStackSize = BUILDER.comment(
                        "合并物品的最大堆叠数量（-1表示无限制）",
                        "Maximum stack size for merged items (-1 = no limit)")
                .defineInRange("maxStackSize", -1, -1, Integer.MAX_VALUE);
        mergeDistance = BUILDER.comment(
                        "物品合并检测半径（方块）",
                        "Item merge detection radius in blocks")
                .defineInRange("mergeDistance", 1.5, 0.1, 10.0);
        showStackCount = BUILDER.comment(
                        "在合并后的物品上显示堆叠数量",
                        "Show stack count on merged items")
                .define("showStackCount", true);
        lockMaxedStacks = BUILDER.comment(
                        "当物品堆叠达到最大时锁定，不再参与合并",
                        "Lock stacks that have reached the maximum size to prevent further merging")
                .define("lockMaxedStacks", true);
        BUILDER.pop();

        BUILDER.push("自定义堆叠 | Custom Stack Size");
        ENABLED = BUILDER.comment(
                        "启用自定义堆叠大小-这里改了出问题的改回去，记住这句话！特别是科技服腐竹！",
                        "Enable custom stack sizes")
                .define("enabled", false);
        MAX_STACK_SIZE = BUILDER.comment(
                        "最大物品堆叠大小 (1-9999)",
                        "Maximum item stack size (1-9999)")
                .defineInRange("maxStackSize", 64, 1, 9999);
        BUILDER.pop();

        BUILDER.push("物品列表 | Item Lists");
        listMode = BUILDER.comment(
                        "0: 禁用 1: 白名单模式 2: 黑名单模式",
                        "0: Disabled, 1: Whitelist, 2: Blacklist")
                .defineInRange("listMode", 0, 0, 2);
        itemList = BUILDER.comment(
                        "白名单/黑名单中的物品注册名列表",
                        "Item registry names for whitelist/blacklist")
                .defineList("itemList", Collections.emptyList(), o -> o instanceof String);
        BUILDER.pop();

        BUILDER.push("物品实体 | Item Entities");
        itemWhitelist = BUILDER.comment(
                        "物品实体白名单",
                        "Item entity whitelist")
                .defineList("itemWhitelist", List.of("minecraft:diamond"), o -> true);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.comment("内存优化 | Memory Optimization").push("memory_optimization");
        MemoryLeakFix_AE2WTLibCreativeTabLeakFix = BUILDER.comment(
                        "内存泄漏修复_AE2WTLibCreativeTabLeakFix",
                        "MemoryLeakFix_AE2WTLib")
                .define("enablememoryleakfixae2", true);
        MemoryLeakFix_ScreenshotByteBufferLeakFix = BUILDER.comment(
                        "内存泄漏修复_ScreenshotByteBufferLeakFix",
                        "MemoryLeakFix_ScreenshotByteBufferLeakFix")
                .define("enablememoryleakfixScreenshotByteBufferLeakFix", true);
        BUILDER.pop();
        BUILDER.comment("调试选项 | Debug Options").push("debug");

        DEBUG_LOGGING = BUILDER.comment(
                        "启用调试日志",
                        "Enable debug logging")
                .define("debug", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean isCullingEnabled() {
        return enableCulling.get();
    }

    public static boolean isEntityCullingEnabled() {
        return isCullingEnabled() && enableEntityCulling.get();
    }

    public static boolean isBlockEntityCullingEnabled() {
        return isCullingEnabled() && enableBlockEntityCulling.get();
    }

    public static boolean isTickStoppingEnabled() {
        return isCullingEnabled() && enableTickStopping.get();
    }

    public static boolean isNameTagCullingEnabled() {
        return isCullingEnabled() && enableNameTagCulling.get();
    }

    public static List<? extends String> getEntityBlacklist() {
        return entityBlacklist.get();
    }

    public enum RenderMode {
        SIMPLE, VANILLA
    }
}