package moddedmite.emi.mixin.client;

import moddedmite.emi.api.EMISearchInput;
import net.minecraft.GuiContainerCreative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.class)
public class GuiContainerCreativeMixin {
    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"), cancellable = true)
    public void handleMouseInput(CallbackInfo ci) {
        if (((EMISearchInput) this).getEMIMouseInput()) {
            ci.cancel();
        }
    }

    @Inject(method = "keyTyped", at = @At(value = "HEAD"), cancellable = true)
    public void blockEMISearchToCreativeSearch(CallbackInfo ci) {
        if (((EMISearchInput)this).getEMISearchInput()) {
            ci.cancel();
        }
    }
}
