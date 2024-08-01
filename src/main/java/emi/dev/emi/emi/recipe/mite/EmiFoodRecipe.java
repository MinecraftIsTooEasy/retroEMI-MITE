package emi.dev.emi.emi.recipe.mite;

import emi.dev.emi.emi.api.recipe.MITEEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.ItemFood;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiFoodRecipe implements EmiRecipe {
	private ResourceLocation TEXTURE = new ResourceLocation("textures/gui/icons.png");
	private final int hunger;
	private final float saturationModifier;
	
	private final EmiStack foodItem;
	
	public EmiFoodRecipe(ItemStack foodStack) {
		ItemFood food = (ItemFood) foodStack.getItem();
		this.hunger = food.getNutrition();
		this.saturationModifier = food.getSatiation(null); //unsure how to implement
		this.foodItem = EmiStack.of(foodStack);
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return MITEEmiRecipeCategories.FOOD;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
		return new SyntheticIdentifier(this);
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiIngredient.of(this.foodItem.getEmiStacks()));
	}
	
	@Override
	public List<EmiIngredient> getCatalysts() {
		return List.of(EmiIngredient.of(this.foodItem.getEmiStacks()));
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return List.of();
	}
	
	@Override
	public boolean supportsRecipeTree() {
		return false;
	}
	
	@Override
	public int getDisplayWidth() {
		return 120;
	}
	
	@Override
	public int getDisplayHeight() {
		return 18;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		int i;
		for (i = 0; i < hunger / 2; i++) {
			widgets.addTexture(TEXTURE, (10 * i) + 25, 5, 9, 9, 16, 27);
			if (!(i - 1 == hunger / 2)) {
				widgets.addTexture(TEXTURE, (10 * i) + 25, 5, 9, 9, 52, 27);
			}
		}
		if (hunger % 2 != 0) {
			int offset = 9 - ((hunger % 2) + 3);
			int haunchUCoord = 62 + offset;
			int haunchXCoord = (10 * i) + 25;
			widgets.addTexture(TEXTURE, (10 * i) + 25, 5, 9, 9, 16, 27);
			widgets.addTexture(TEXTURE, haunchXCoord + offset + 1, 5, 9 - offset, 9, haunchUCoord, 27);
		}
		widgets.addSlot(foodItem, 0, 0).recipeContext(this);
		
	}
	
	public int getHunger() {
		return hunger;
	}
	
	public EmiStack getFoodItem() {
		return foodItem;
	}
}
