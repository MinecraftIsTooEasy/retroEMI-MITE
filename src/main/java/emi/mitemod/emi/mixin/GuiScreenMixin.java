package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMISearchInput;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.GuiInventory;
import net.minecraft.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiScreen.class)
public class GuiScreenMixin implements EMISearchInput {
    @Unique
    private boolean emiSearchInput = false;
    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    public void handleMouseInputEMI(CallbackInfo ci) {
        RetroEMI.handleMouseInput();
    }

    @Inject(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", shift = At.Shift.AFTER))
    public void handleKeyboardInputEMI(CallbackInfo ci) {
         emiSearchInput = RetroEMI.handleKeyboardInput();
    }

    @Inject(method = "allowsImposedChat", at = @At("RETURN"), cancellable = true)
    public void allowsImposedChat(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof GuiInventory) {
            cir.setReturnValue(!emiSearchInput);
        }
    }

    @Override
    public boolean getEMISearchInput() {
        return emiSearchInput;
    }
}
