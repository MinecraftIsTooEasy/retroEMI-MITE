package dev.emi.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.config.EmiConfig;
import moddedmite.emi.api.EMIShapelessRecipes;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.text.MutableText;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.IRecipe;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import net.minecraft.ShapelessRecipes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class EmiShapelessRecipe extends EmiCraftingRecipe {

	private final ShapelessRecipes shapeless_recipe;

	public EmiShapelessRecipe(EMIShapelessRecipes recipe, ShapelessRecipes shapelessRecipes, float craftingDifficulty) {
		super(((List<ItemStack>) recipe.getRecipeItems()).stream().map(RetroEMI::wildcardIngredient).collect(Collectors.toList()),
				EmiStack.of(EmiPort.getOutput((IRecipe) recipe)), new SyntheticIdentifier(recipe), recipe.getSecondaryOutput(null), craftingDifficulty);
        EmiShapedRecipe.setRemainders(input, (IRecipe) recipe);
		this.shapeless_recipe = shapelessRecipes;
	}


	@Override
	public Material getCraftLevel() {
		return this.shapeless_recipe.getMaterialToCheckToolBenchHardnessAgainst();
	}

	@Override
	public boolean canFit(int width, int height) {
		return input.size() <= width * height;
	}
}
