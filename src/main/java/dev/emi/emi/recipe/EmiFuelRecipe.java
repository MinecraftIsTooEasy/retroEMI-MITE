package dev.emi.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.Item;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiFuelRecipe implements EmiRecipe {
	private final EmiIngredient stack;
	private final int time;
	private final int heat;
	private final ResourceLocation id;
	
	public EmiFuelRecipe(EmiIngredient stack, int time, int heat, ResourceLocation id) {
		this.stack = stack;
		this.time = time;
		this.heat = heat;
		this.id = id;
		if (stack.getEmiStacks().get(0).getItemStack().getItem().equals(Item.bucketIronLava)) {
			stack.getEmiStacks().get(0).setRemainder(EmiStack.of(Item.bucketIronEmpty));
		}
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.FUEL;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(stack);
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return List.of();
	}
	
	@Override
	public int getDisplayWidth() {
		return 144;
	}
	
	@Override
	public int getDisplayHeight() {
		return 18;
	}
	
	@Override
	public boolean supportsRecipeTree() {
		return false;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_FLAME, 1, 1);
		widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 1, 1, 1000 * time / 20, false, true, true);
		widgets.addSlot(stack, 18, 0).recipeContext(this);
		widgets.addText(EmiPort.translatable("emi.fuel_number.items", String.format("%.1f", (time / 200f))), 38, 0, -1, true);
		widgets.addText(EmiPort.translatable("emi.fuel_heat.items", String.format("%1d", heat)), 38, 10, -1, true);
	}
}
