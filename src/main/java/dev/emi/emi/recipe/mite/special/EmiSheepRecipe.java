package dev.emi.emi.recipe.mite.special;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiSheepRecipe implements EmiRecipe {
	@Override
	public EmiRecipeCategory getCategory() {
		return null;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
		return null;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return null;
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return null;
	}
	
	@Override
	public int getDisplayWidth() {
		return 0;
	}
	
	@Override
	public int getDisplayHeight() {
		return 0;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
	
	}
}
