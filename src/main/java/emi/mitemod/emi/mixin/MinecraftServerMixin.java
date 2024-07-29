package emi.mitemod.emi.mixin;

import emi.mitemod.emi.util.MinecraftServerEMI;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void addIsServer(String[] par0ArrayOfStr, CallbackInfo ci) {
        MinecraftServerEMI.isServer = true;
    }
}
