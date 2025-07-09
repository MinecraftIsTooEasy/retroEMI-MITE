package moddedmite.emi.mixin.client;

import dev.emi.emi.EMIPostInit;
import dev.emi.emi.EmiPort;
import dev.emi.emi.screen.EmiScreenManager;
import moddedmite.emi.api.EMIMinecraft;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements EMIMinecraft {
    @Shadow public Timer timer;
    @Shadow private ReloadableResourceManager mcResourceManager;
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;
    @Shadow public abstract void displayGuiScreen(GuiScreen par1GuiScreen);

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;loadScreen()V", shift = At.Shift.BEFORE))
    private void registerReloadListeners(CallbackInfo ci) {
        //Added with EMI
        EmiPort.registerReloadListeners(this.mcResourceManager);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initEMIClient(Session par1Session, int par2, int par3, boolean par4, boolean par5, File par6File, File par7File, File par8File, Proxy par9Proxy, String par10Str, CallbackInfo ci) {
        EMIPostInit.initEMI();
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiScreen;allowsImposedChat()Z"))
    public boolean allowsImposedChat(GuiScreen guiScreen) {
        return !EmiScreenManager.search.isFocused() && guiScreen.allowsImposedChat();
    }
}
