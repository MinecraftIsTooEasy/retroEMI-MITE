package emi.dev.emi.emi.recipe.btw.special;

import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
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
