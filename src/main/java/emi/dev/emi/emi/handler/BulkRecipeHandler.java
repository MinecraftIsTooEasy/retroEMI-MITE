package emi.dev.emi.emi.handler;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.ContainerFurnace;
import net.minecraft.Slot;

import java.util.List;

public class BulkRecipeHandler implements StandardRecipeHandler<ContainerFurnace> {
	
	@Override
	public List<Slot> getInputSources(ContainerFurnace handler) {
		List<Slot> list = Lists.newArrayList();
		for (int i = 0; i < 27; i++) {
			list.add(handler.getSlot(i));
		}
		int invStart = 27;
		for (int i = invStart; i < invStart + 36; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public List<Slot> getCraftingSlots(ContainerFurnace handler) {
		List<Slot> list = Lists.newArrayList();
		for (int i = 0; i < 27; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe.supportsRecipeTree() && recipe.getCategory() == BTWEmiRecipeCategories.CAULDRON || recipe.getCategory() == BTWEmiRecipeCategories.CRUCIBLE;
	}
}
