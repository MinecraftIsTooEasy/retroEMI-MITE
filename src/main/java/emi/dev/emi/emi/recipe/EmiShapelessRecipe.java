package emi.dev.emi.emi.recipe;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.mitemod.emi.api.EMIShapelessRecipes;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.IRecipe;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import net.minecraft.ShapelessRecipes;

import java.util.List;
import java.util.stream.Collectors;

public class EmiShapelessRecipe extends EmiCraftingRecipe {

	private final ShapelessRecipes shapeless_recipe;

	public EmiShapelessRecipe(EMIShapelessRecipes recipe, ShapelessRecipes shapelessRecipes) {
		super(((List<ItemStack>) recipe.getRecipeItems()).stream().map(RetroEMI::wildcardIngredient).collect(Collectors.toList()),
				EmiStack.of(EmiPort.getOutput((IRecipe) recipe)), new SyntheticIdentifier(recipe), recipe.getSecondaryOutput(null));
		EmiShapedRecipe.setRemainders(input, (IRecipe) recipe, false);
		this.shapeless_recipe = shapelessRecipes;
	}


	@Override
	public Material craftLevel() {
		return this.shapeless_recipe.getMaterialToCheckToolBenchHardnessAgainst();
	}

	@Override
	public boolean canFit(int width, int height) {
		return input.size() <= width * height;
	}
}
