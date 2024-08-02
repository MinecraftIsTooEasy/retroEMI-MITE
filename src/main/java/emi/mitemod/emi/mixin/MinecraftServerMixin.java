package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.EMIPostInit;
import emi.mitemod.emi.util.MinecraftServerEMI;
import net.minecraft.Session;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void addIsServer(String[] par0ArrayOfStr, CallbackInfo ci) {
        MinecraftServerEMI.isServer = true;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(File par1File, CallbackInfo ci) {
        EMIPostInit.InRelauncher.init();
    }
}
