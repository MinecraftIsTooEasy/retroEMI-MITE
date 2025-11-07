package moddedmite.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.runtime.EmiLog;
import moddedmite.emi.api.recipe.MITEEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.text.Text;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class EmiFoodRecipe implements EmiRecipe {
	private final ResourceLocation VANILLA = new ResourceLocation("textures/gui/icons.png");
	private final ResourceLocation TEXTURE = new ResourceLocation("emi", "textures/gui/icons_food.png");

	private int y = 5;
	private final int nutrition;
	private final int saturation;
	private final int phytonutrients;
	private final int protein;
	private final int sugar;
	private final EmiStack foodItem;
	
	public EmiFoodRecipe(ItemStack foodStack) {
		Item food = foodStack.getItem();
		this.nutrition = food.getNutrition();
		this.saturation = food.getSatiation(null); //unsure how to implement
		this.phytonutrients = food.getPhytonutrients() / 8000;
		this.protein = food.getProtein() / 8000;
		this.sugar = food.getSugarContent() / 1000;

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
		int rowHeight = 10;
		int padding = 8;
		return Math.max(10, getVisibleRowCount() * rowHeight + padding);
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		drawFoodValueBar(widgets, nutrition, 16, 52, 61, 27, VANILLA);
		drawFoodValueBar(widgets, saturation, 34, 43, 52, 18, TEXTURE);
		drawFoodValueBar(widgets, phytonutrients, 61, 70, 79, 18, TEXTURE);
		drawFoodValueBar(widgets, protein, 88, 97, 106, 18, TEXTURE);

		for (int i = 0; i < sugar; i++) {
			int x = (10 * i) + 25;
			widgets.addTexture(TEXTURE, x, y, 9, 9, 115, 18);
			widgets.addTexture(TEXTURE, x, y, 9, 9, 124, 18);
		}
		checkY(sugar);
		this.y = 5;

		Map<String, Integer> foodInfo = Map.of(
				"emi.nutrition.items", nutrition,
				"emi.saturation.items", saturation,
				"emi.phytonutrients.items", phytonutrients * 8000,
				"emi.protein.items", protein * 8000,
				"emi.sugar.items", sugar * 1000
		);

		SlotWidget slot = widgets.addSlot(foodItem, 2, this.getDisplayHeight() / 2 - 8).recipeContext(this);
		foodInfo.entrySet().stream().filter(entry -> entry.getValue() > 0)
				.forEach(entry -> slot.appendTooltip(() ->
						TooltipComponent.of(Text.translatable(entry.getKey(), entry.getValue()))));
	}
	
	public int getNutrition() {
		return nutrition;
	}
	
	public EmiStack getFoodItem() {
		return foodItem;
	}

	private void checkY(int value) {
		if (value > 0) {
			this.y += 10;
		}
	}

	private int getVisibleRowCount() {
		int rows = 0;
		if (nutrition > 0) rows++;
		if (saturation > 0) rows++;
		if (phytonutrients > 0) rows++;
		if (protein > 0) rows++;
		if (sugar > 0) rows++;
		return rows;
	}

	private void drawFoodValueBar(WidgetHolder widgets, int amount, int bgU, int fullU, int halfU, int v, ResourceLocation texture) {
		for (int i = 0; i < amount / 2; i++) {
			int x = (10 * i) + 25;
			widgets.addTexture(texture, x, y, 9, 9, bgU, v);
			widgets.addTexture(texture, x, y, 9, 9, fullU, v);
		}

		if (amount % 2 != 0) {
			int x = (10 * (amount / 2)) + 25;
			widgets.addTexture(texture, x, y, 9, 9, bgU, v);
			widgets.addTexture(texture, x, y, 9, 9, halfU, v);
		}

		checkY(amount);
	}
}
