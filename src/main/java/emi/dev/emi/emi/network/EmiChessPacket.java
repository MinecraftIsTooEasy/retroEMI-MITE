package emi.dev.emi.emi.network;

import emi.dev.emi.emi.chess.EmiChess;
import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ServerPlayer;
import net.minecraft.ResourceLocation;

public abstract class EmiChessPacket implements EmiPacket {
	protected final String uuid;
	protected final byte type, start, end;
	
	public EmiChessPacket(String uuid, byte type, byte start, byte end) {
		this.uuid = uuid;
		this.type = type;
		this.start = start;
		this.end = end;
	}
	
	public EmiChessPacket(PacketByteBuf buf) {
		this(buf.readString(), buf.readByte(), buf.readByte(), buf.readByte());
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(uuid);
		buf.writeByte(type);
		buf.writeByte(start);
		buf.writeByte(end);
	}
	
	@Override
	public ResourceLocation getId() {
		return EmiNetwork.CHESS;
	}
	
	public static class S2C extends EmiChessPacket {
		
		public S2C(String uuid, byte type, byte start, byte end) {
			super(uuid, type, start, end);
		}
		
		public S2C(PacketByteBuf buf) {
			super(buf);
		}
		
		@Override
		public void apply(EntityPlayer player) {
			EmiChess.receiveNetwork(uuid, type, start, end);
		}
	}
	
	public static class C2S extends EmiChessPacket {
		
		public C2S(String uuid, byte type, byte start, byte end) {
			super(uuid, type, start, end);
		}
		
		public C2S(PacketByteBuf buf) {
			super(buf);
		}
		
		@Override
		public void apply(EntityPlayer player) {
			EntityPlayer opponent = player.worldObj.getPlayerEntityByName(uuid);
			if (opponent instanceof ServerPlayer spe) {
				EmiNetwork.sendToClient(spe, new S2C(player.getCommandSenderName(), type, start, end));
			}
		}
	}
}
