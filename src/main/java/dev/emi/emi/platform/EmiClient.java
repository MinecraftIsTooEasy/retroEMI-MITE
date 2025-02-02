package dev.emi.emi.platform;

import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.network.EmiNetwork;
import dev.emi.emi.network.FillRecipeC2SPacket;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.Container;
import net.minecraft.GuiContainer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;

import java.util.List;

public class EmiClient implements ClientModInitializer{
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

	@Override
	public void onInitializeClient() {

	}
}
