package emi.dev.emi.emi.platform;

import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.network.EmiNetwork;
import emi.dev.emi.emi.network.FillRecipeC2SPacket;
import net.minecraft.Container;
import net.minecraft.GuiContainer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

public class EmiClient {
	public static boolean onServer = false;

	public static void init() {
		EmiConfig.loadConfig();
	}

	public static <T extends Container> void sendFillRecipe(StandardRecipeHandler<T> handler, GuiContainer screen,
															int syncId, int action, List<ItemStack> stacks, EmiRecipe recipe) {
		T screenHandler = (T)screen.inventorySlots;
		List<Slot> crafting = handler.getCraftingSlots(recipe, screenHandler);
		Slot output = handler.getOutputSlot(screenHandler);
		EmiNetwork.sendToServer(new FillRecipeC2SPacket(screenHandler, action, handler.getInputSources(screenHandler), crafting, output, stacks));
	}
}
