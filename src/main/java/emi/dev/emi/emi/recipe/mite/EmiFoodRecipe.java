package emi.dev.emi.emi.recipe.mite;

import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.MITEEmiRecipeCategories;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.Item;
import net.minecraft.ItemFood;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiFoodRecipe implements EmiRecipe {
	private ResourceLocation VANILLA = new ResourceLocation("textures/gui/icons.png");
	private ResourceLocation TEXTURE = new ResourceLocation("textures/gui/icons_food.png");
	private ResourceLocation BEEF = new ResourceLocation("textures/items/beef_cooked.png");
	private ResourceLocation CARROT = new ResourceLocation("textures/items/carrot.png");
	private ResourceLocation SUGAR = new ResourceLocation("textures/items/sugar.png");

	private int y = 5;
	private final int hunger;
	private final int saturationModifier;
	private final int phytonutrients;
	private final int protein;
	private final int sugar;
	private final EmiStack foodItem;
	
	public EmiFoodRecipe(ItemStack foodStack) {
		Item food = foodStack.getItem();
		this.hunger = food.getNutrition();
		this.saturationModifier = food.getSatiation(null); //unsure how to implement
		this.phytonutrients = food.getPhytonutrients() / 8000;
		this.protein = food.getProtein() / 8000;
		this.sugar = food.getSugarContent() / 8000;

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
		return 58;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		int i;

		for (i = 0; i < hunger / 2; i++) {
			widgets.addTexture(VANILLA, (10 * i) + 25, y, 9, 9, 16, 27);
			if (!(i - 1 == hunger / 2)) {
				widgets.addTexture(VANILLA, (10 * i) + 25, y, 9, 9, 52, 27);
			}
		}
		if (hunger % 2 != 0) {
            int haunchUCoord = 61;
			int haunchXCoord = (10 * i) + 25;
			widgets.addTexture(VANILLA, haunchXCoord, y, 9, 9, 16, 27);
			widgets.addTexture(VANILLA, haunchXCoord, y, 9, 9, haunchUCoord, 27);
		}
		checkY(hunger);

		for (i = 0; i < saturationModifier / 2; i++) {
			widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 34, 18);
			if (!(i - 1 == saturationModifier / 2)) {
				widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 43, 18);
			}
		}
		if (saturationModifier % 2 != 0) {
            int haunchUCoord = 52;
			int haunchXCoord = (10 * i) + 25;
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, 34, 18);
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, haunchUCoord , 27 - 9);
		}
		checkY(saturationModifier);

		for (i = 0; i < phytonutrients / 2; i++) {
			widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 61, 18);
			if (!(i - 1 == phytonutrients / 2)) {
				widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 70, 18);
			}
		}
		if (phytonutrients % 2 != 0) {
			int haunchUCoord = 79;
			int haunchXCoord = (10 * i) + 25;
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, 61, 18);
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, haunchUCoord , 27 - 9);
		}
		checkY(phytonutrients);

		for (i = 0; i < protein / 2; i++) {
			widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 88, 18);
			if (!(i - 1 == protein / 2)) {
				widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 97, 18);
			}
		}
		if (protein % 2 != 0) {
			int haunchUCoord = 106;
			int haunchXCoord = (10 * i) + 25;
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, 88, 18);
			widgets.addTexture(TEXTURE, haunchXCoord, y, 9, 9, haunchUCoord , 27 - 9);
		}
		checkY(protein);

		for (i = 0; i < sugar; i++) {
			widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 115, 18);
			widgets.addTexture(TEXTURE, (10 * i) + 25, y, 9, 9, 124, 18);
		}

		checkY(sugar);

		this.y = 5;
		widgets.addSlot(foodItem, 0, 0).recipeContext(this);
	}
	
	public int getHunger() {
		return hunger;
	}
	
	public EmiStack getFoodItem() {
		return foodItem;
	}

	public void checkY(int value) {
		if (value > 0) {
			this.y += 10;
		}
	}
}
