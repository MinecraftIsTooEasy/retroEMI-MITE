package emi.dev.emi.emi.recipe;

import emi.dev.emi.emi.api.plugin.VanillaPlugin;
import emi.dev.emi.emi.api.recipe.EmiIngredientRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.EmiResolutionRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import net.minecraft.ResourceLocation;

import java.util.List;

public class EmiSyntheticIngredientRecipe extends EmiIngredientRecipe {
	private final EmiIngredient ingredient;
	
	public EmiSyntheticIngredientRecipe(EmiIngredient ingredient) {
		this.ingredient = ingredient;
	}
	
	@Override
	protected EmiIngredient getIngredient() {
		return ingredient;
	}
	
	@Override
	protected List<EmiStack> getStacks() {
		return ingredient.getEmiStacks();
	}
	
	@Override
	protected EmiRecipe getRecipeContext(EmiStack stack, int offset) {
		return new EmiResolutionRecipe(ingredient, stack);
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaPlugin.INGREDIENT;
	}
	
	@Override
	public ResourceLocation getId() {
		return null;
	}
}
