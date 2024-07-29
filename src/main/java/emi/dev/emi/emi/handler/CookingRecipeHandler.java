package emi.dev.emi.emi.handler;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.ContainerFurnace;
import net.minecraft.Slot;

import java.util.List;

public class CookingRecipeHandler<T extends ContainerFurnace> implements StandardRecipeHandler<T> {
	private final EmiRecipeCategory category;
	
	public CookingRecipeHandler(EmiRecipeCategory category) {
		this.category = category;
	}
	
	@Override
	public List<Slot> getInputSources(T handler) {
		List<Slot> list = Lists.newArrayList();
		list.add(handler.getSlot(0));
		int invStart = 3;
		for (int i = invStart; i < invStart + 36; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public List<Slot> getCraftingSlots(T handler) {
		return List.of((Slot) handler.inventorySlots.get(0));
	}
	
	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe.getCategory() == category && recipe.supportsRecipeTree();
	}
}
