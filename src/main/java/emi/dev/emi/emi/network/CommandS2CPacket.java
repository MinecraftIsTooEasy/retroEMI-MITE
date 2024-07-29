package emi.dev.emi.emi.network;


import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.bom.BoM;
import emi.dev.emi.emi.registry.EmiCommands;
import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class CommandS2CPacket implements EmiPacket {
	private final byte type;
	private final ResourceLocation id;
	
	public CommandS2CPacket(byte type, ResourceLocation id) {
		this.type = type;
		this.id = id;
	}
	
	
	public CommandS2CPacket(PacketByteBuf buf) {
		type = buf.readByte();
		if (type == EmiCommands.VIEW_RECIPE || type == EmiCommands.TREE_GOAL || type == EmiCommands.TREE_RESOLUTION) {
			String path = buf.readString();
			String domain = buf.readString();
			id = new ResourceLocation(path, domain);
		}
		else {
			id = null;
		}
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(type);
		if (type == EmiCommands.VIEW_RECIPE || type == EmiCommands.TREE_GOAL || type == EmiCommands.TREE_RESOLUTION) {
			buf.writeString(id.getResourcePath());
			buf.writeString(id.getResourceDomain());
		}
	}
	
	@Override
	public void apply(EntityPlayer player) {
		if (type == EmiCommands.VIEW_RECIPE) {
			EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
			if (recipe != null) {
				EmiApi.displayRecipe(recipe);
			}
		}
		else if (type == EmiCommands.VIEW_TREE) {
			EmiApi.viewRecipeTree();
		}
		else if (type == EmiCommands.TREE_GOAL) {
			EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
			if (recipe != null) {
				BoM.setGoal(recipe);
			}
		}
		else if (type == EmiCommands.TREE_RESOLUTION) {
			EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
			if (recipe != null && BoM.tree != null) {
				for (EmiStack stack : recipe.getOutputs()) {
					BoM.tree.addResolution(stack, recipe);
				}
			}
		}
	}
	
	@Override
	public ResourceLocation getId() {
		return EmiNetwork.COMMAND;
	}
}
