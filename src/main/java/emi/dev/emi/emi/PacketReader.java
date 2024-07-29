package emi.dev.emi.emi;

import emi.dev.emi.emi.network.EmiPacket;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PacketReader {

    public static final Map<String, Function<PacketByteBuf, EmiPacket>> clientReaders = new HashMap<>();
    public static final Map<String, Function<PacketByteBuf, EmiPacket>> serverReaders = new HashMap<>();

    public static void registerServerPacketReader(ResourceLocation id, Function<PacketByteBuf, EmiPacket> reader) {
        serverReaders.put(RetroEMI.compactify(id), reader);
    }

    public static void registerClientPacketReader(ResourceLocation id, Function<PacketByteBuf, EmiPacket> reader) {
        clientReaders.put(RetroEMI.compactify(id), reader);
    }
}
