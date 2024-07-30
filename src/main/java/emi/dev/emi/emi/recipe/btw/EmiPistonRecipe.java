package emi.dev.emi.emi.recipe.btw;

import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiPistonRecipe implements EmiRecipe {
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/recipe/btwwidgets.png");
	public static final EmiTexture PISTON = new EmiTexture(BACKGROUND, 0, 16, 20, 16);
	
	private final ResourceLocation id;
	private final EmiIngredient input;
	private final EmiStack output;
	private final int meta;
	
	public EmiPistonRecipe(ResourceLocation id, ItemStack[] input, int meta, Block output) {
		this.id = id;
		this.input = EmiStack.of(input[0]);
		this.output = EmiStack.of(output);
		this.meta = meta;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return BTWEmiRecipeCategories.PISTON;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		ItemStack out = output.getItemStack();
		out.setItemDamage(meta);
		return EmiStack.of(out).getEmiStacks();
	}
	
	@Override
	public int getDisplayWidth() {
		return 119;
	}
	
	@Override
	public int getDisplayHeight() {
		return 28;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Piston animation
		widgets.addTexture(BACKGROUND, 3, 5, 12, 16, 0, 0);
		widgets.addCustomAnimatedTexture(PISTON, 15, 5, 3, 9);
		
		widgets.addSlot(input, 40, 5).appendTooltip(Text.translatable("emi.info.piston_crushing_input"));
		
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 5);
		widgets.addSlot(output, 87, 1).large(true).appendTooltip(Text.translatable("emi.info.piston_crushing_output")).recipeContext(this);
	}
}
