package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.PacketReader;
import emi.dev.emi.emi.network.EmiPacket;
import emi.dev.emi.emi.platform.EmiClient;
import emi.dev.emi.emi.runtime.EmiLog;
import emi.dev.emi.emi.runtime.EmiReloadManager;
import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.Minecraft;
import net.minecraft.NetClientHandler;
import net.minecraft.Packet250CustomPayload;
import net.minecraft.Packet5PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.function.Function;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload, CallbackInfo ci) {
        // EMI edit (Taken from retro emi, might want to rewrite how packets are handled?)
        Function<PacketByteBuf, EmiPacket> reader = PacketReader.clientReaders.get(par1Packet250CustomPayload.channel);
        if (reader != null) {
            var epkt = reader.apply(PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data))));
            epkt.apply(Minecraft.getMinecraft().thePlayer);
        }
        // End EMI edit
    }

    @Inject(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/INetworkManager;networkShutdown(Ljava/lang/String;[Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    public void disconnect(CallbackInfo ci) {
        EmiLog.info("Disconnecting from server, EMI data cleared");
        EmiReloadManager.clear();
        EmiClient.onServer = false;
    }

    @Inject(method = "handlePlayerInventory", at = @At("HEAD"), cancellable = true)
    public void handlePlayerInventory(Packet5PlayerInventory par1Packet5PlayerInventory, CallbackInfo ci) {
        if(par1Packet5PlayerInventory.full_inventory) {
            ci.cancel();
        }
    }
}
