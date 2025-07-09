package dev.emi.emi.network;

import dev.emi.emi.runtime.EmiLog;
import shims.java.com.unascribed.retroemi.ItemStacks;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.*;

public class CreateItemC2SPacket implements EmiPacket {
	private final int mode;
	private final ItemStack stack;
	
	public CreateItemC2SPacket(int mode, ItemStack stack) {
		this.mode = mode;
		this.stack = stack;
	}
	
	public CreateItemC2SPacket(PacketByteBuf buf) {
		this(buf.readByte(), buf.readItemStack());
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(mode);
		buf.writeItemStack(stack);
	}
	
	@Override
	public void apply(EntityPlayer player) {
		if (player instanceof ServerPlayer esp &&
				((esp.mcServer.getConfigurationManager().isPlayerOpped(esp.getCommandSenderName())) || player.capabilities.isCreativeMode) &&
				player.openContainer != null) {
			if (ItemStacks.isEmpty(stack)) {
				if (mode == 1) {
					player.inventory.setItemStack(null);
				}
			} else {
				EmiLog.info(player.getEntityName() + " cheated in " + stack);
				if (mode == 0) {
					RetroEMI.offerOrDrop(player, stack);
				}
				else if (mode == 1) {
					player.inventory.setItemStack(stack);
					esp.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(-1, 0, stack));
				}
			}
		}
	}
	
	@Override
	public ResourceLocation getId() {
		return EmiNetwork.CREATE_ITEM;
	}
}
