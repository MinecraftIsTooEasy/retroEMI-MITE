package moddedmite.emi.mixin.server;

import dev.emi.emi.EMIPostInit;
import moddedmite.emi.util.MinecraftServerHelper;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void addIsServer(String[] par0ArrayOfStr, CallbackInfo ci) {
        MinecraftServerHelper.isServer = true;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initEMIServer(File par1File, CallbackInfo ci) {
        EMIPostInit.initEMI();
    }
}
