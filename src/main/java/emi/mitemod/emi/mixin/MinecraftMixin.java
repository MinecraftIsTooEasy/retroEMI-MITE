package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.EmiPort;
import emi.mitemod.emi.api.EMIMinecraft;
import net.minecraft.Minecraft;
import net.minecraft.ReloadableResourceManager;
import net.minecraft.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements EMIMinecraft {
    @Shadow public Timer timer = new Timer(20.0f);
    @Shadow private ReloadableResourceManager mcResourceManager;

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;loadScreen()V", shift = At.Shift.BEFORE))
    private void startGameInjectEMI(CallbackInfo ci) {
        //Added with EMI
        EmiPort.registerReloadListeners(this.mcResourceManager);
    }
}
