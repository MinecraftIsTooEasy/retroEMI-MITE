package emi.dev.emi.emi.recipe.mite.special;

import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.List;

public class EmiFishingRecipe extends EmiCraftingRecipe {
	
	public EmiFishingRecipe(ItemStack input, String id) {
		super(getIngredients(input), EmiStack.of(Item.fishingRodFlint), new ResourceLocation("fishingRod", id), null);
	}
	
	private static List<EmiIngredient> getIngredients(ItemStack input) {
		return List.of(EmiStack.of(Item.fishingRodFlint), EmiStack.of(input));
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}
}
