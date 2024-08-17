package emi.moddedmite.emi.mixin;

import emi.dev.emi.emi.network.EmiNetwork;
import emi.dev.emi.emi.network.PingS2CPacket;
import net.minecraft.IntegratedServer;
import net.minecraft.ServerConfigurationManager;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {

    @Shadow @Final private MinecraftServer mcServer;

    @Inject(method = "playerLoggedIn", at = @At(value = "INVOKE", target = "Lnet/minecraft/ServerConfigurationManager;sendPacketToAllPlayers(Lnet/minecraft/Packet;)V", shift = At.Shift.AFTER))
    private void loggedInEMIPack(ServerPlayer par1EntityPlayerMP, CallbackInfo ci) {
        EmiNetwork.sendToClient(par1EntityPlayerMP, new PingS2CPacket(this.mcServer.isDedicatedServer() || (this.mcServer instanceof IntegratedServer integratedServer && integratedServer.getPublic())));
    }
}
