package emi.dev.emi.emi.recipe.btw;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiProgressiveRecipe implements EmiRecipe {
	private final ResourceLocation id;
	private final EmiIngredient input;
	private final EmiIngredient output;
	
	public EmiProgressiveRecipe(ResourceLocation id, ItemStack input, ItemStack output) {
		this.id = id;
		this.input = RetroEMI.wildcardIngredient(input);
		this.output = RetroEMI.wildcardIngredient(output);
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return BTWEmiRecipeCategories.PROGRESSIVE;
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
		return output.getEmiStacks();
	}
	
	@Override
	public int getDisplayWidth() {
		return 78;
	}
	
	@Override
	public int getDisplayHeight() {
		return 22;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addFillingArrow(27, 2, 3000 * 20).tooltip((mx, my) -> {
			return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("emi.btw.progressive.time", 200 / 20f))));
		});
		widgets.addSlot(input, 5, 2);
		widgets.addSlot(output, 55, 2);
	}
}
