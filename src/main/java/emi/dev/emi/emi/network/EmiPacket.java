package emi.dev.emi.emi.network;

import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public interface EmiPacket {
	
	void write(PacketByteBuf buf);
	
	void apply(EntityPlayer player);
	
	ResourceLocation getId();
}
