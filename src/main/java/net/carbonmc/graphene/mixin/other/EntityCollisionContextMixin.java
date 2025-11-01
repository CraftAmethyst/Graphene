package net.carbonmc.graphene.mixin.other;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntityCollisionContext.class)
public class EntityCollisionContextMixin {

    @Mutable
    @Shadow
    @Final
    private ItemStack heldItem;

    @Mutable
    @Shadow
    @Final
    private Predicate<FluidState> canStandOnFluid;

    @Shadow
    @Final
    private Entity entity;

    @Unique
    private boolean graphene$heldItemInitialized = false;
    @Unique
    private boolean graphene$fluidPredicateInitialized = false;

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/Entity;)V",
            at = @At("RETURN")
    )
    private void onConstructed(Entity entity, CallbackInfo ci) {
        this.heldItem = null;
        this.canStandOnFluid = null;
    }

    @Inject(
            method = "isHoldingItem",
            at = @At("HEAD")
    )
    private void initHeldItemOnDemand(Item item, CallbackInfoReturnable<Boolean> cir) {
        if (!graphene$heldItemInitialized) {
            graphene$initializeHeldItem();
        }
    }

    @Inject(
            method = "canStandOnFluid",
            at = @At("HEAD")
    )
    private void initFluidPredicateOnDemand(FluidState p_205115_, FluidState p_205116_, CallbackInfoReturnable<Boolean> cir) {
        if (!graphene$fluidPredicateInitialized) {
            graphene$initializeFluidPredicate();
        }
    }

    @Unique
    private void graphene$initializeHeldItem() {
        if (this.entity instanceof LivingEntity livingEntity) {
            this.heldItem = livingEntity.getMainHandItem();
        } else {
            this.heldItem = ItemStack.EMPTY;
        }
        this.graphene$heldItemInitialized = true;
    }

    @Unique
    private void graphene$initializeFluidPredicate() {
        if (this.entity instanceof LivingEntity livingEntity) {
            this.canStandOnFluid = livingEntity::canStandOnFluid;
        } else {
            this.canStandOnFluid = (fluid) -> false;
        }
        this.graphene$fluidPredicateInitialized = true;
    }
}