package emi.mitemod.emi.mixin;

import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    public void handleMouseInputEMI(CallbackInfo ci) {
        RetroEMI.handleMouseInput();
    }

    @Inject(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", shift = At.Shift.AFTER))
    public void handleKeyboardInputEMI(CallbackInfo ci) {
        RetroEMI.handleKeyboardInput();
    }
}
