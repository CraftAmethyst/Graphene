package net.carbonmc.graphene.mixin.client.lang;

import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.class)
public interface ButtonAccessor {
    @Accessor("onPress")
    Button.OnPress getOnPress();

    @Accessor("onPress")
    void setOnPress(Button.OnPress onPress);
}