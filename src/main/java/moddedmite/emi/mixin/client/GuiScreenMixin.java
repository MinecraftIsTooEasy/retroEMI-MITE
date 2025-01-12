package moddedmite.emi.mixin.client;

import moddedmite.emi.api.EMISearchInput;
import shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin implements EMISearchInput {
    @Unique
    private boolean emiSearchInput = false;
    @Unique
    private boolean emiMouseInput = false;

    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    public void handleMouseInputEMI(CallbackInfo ci) {
        this.emiMouseInput = RetroEMI.handleMouseInput();
    }

    @Inject(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", shift = At.Shift.AFTER))
    public void handleKeyboardInputEMI(CallbackInfo ci) {
        this.emiSearchInput = RetroEMI.handleKeyboardInput();
    }

    @Override
    public boolean getEMISearchInput() {
        return this.emiSearchInput;
    }

    @Override
    public boolean getEMIMouseInput() {
        return this.emiMouseInput;
    }
}
