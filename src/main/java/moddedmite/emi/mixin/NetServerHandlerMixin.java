package moddedmite.emi.mixin;

import dev.emi.emi.PacketReader;
import dev.emi.emi.network.EmiPacket;
import shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.NetHandler;
import net.minecraft.NetServerHandler;
import net.minecraft.Packet250CustomPayload;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.function.Function;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin extends NetHandler {
    @Shadow public ServerPlayer playerEntity;

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload, CallbackInfo ci) {
        // EMI edit (Taken from retro emi, might want to rewrite how packets are handled?)
        Function<PacketByteBuf, EmiPacket> reader = PacketReader.serverReaders.get(par1Packet250CustomPayload.channel);
        if (reader != null) {
            var epkt = reader.apply(PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data))));
            epkt.apply(playerEntity);
        }
        // End EMI edit
    }

}
