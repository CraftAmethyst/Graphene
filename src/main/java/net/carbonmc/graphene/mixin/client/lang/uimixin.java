package net.carbonmc.graphene.mixin.client.lang;

import net.carbonmc.graphene.lang.flang;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageSelectScreen.class)
public abstract class uimixin extends OptionsSubScreen {

    public uimixin(Screen p_96284_, Options p_96285_, Component p_96286_) {
        super(p_96284_, p_96285_, p_96286_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof Button button) {
                ButtonAccessor accessor = (ButtonAccessor) button;
                Button.OnPress originalPress = accessor.getOnPress();
                accessor.setOnPress((p_96099_) -> {
                    flang.langReload = true;
                    originalPress.onPress(p_96099_);
                });
            }
        }
    }
}