package emi.dev.emi.emi.recipe.btw;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmiHopperRecipe implements EmiRecipe {
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/recipe/hopper.png");
	
	private final ResourceLocation id;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public EmiHopperRecipe(ResourceLocation hopperId, ItemStack input, ItemStack filterItem, ItemStack filterOut, ItemStack hopOut, boolean containsSouls) {
		this.id = hopperId;
		this.inputs = convertInput(input, filterItem);
		this.outputs = convertOutput(hopOut, filterOut);
	}
	
	public static List<EmiStack> convertOutput(ItemStack hopperOut, ItemStack filterOut) {
		List<EmiStack> list = Lists.newArrayList();
		if (!ItemStacks.isEmpty(hopperOut)) {
			list.add(EmiStack.of(hopperOut));
		}
		list.add(EmiStack.of(filterOut));
		return list;
	}
	
	public static List<EmiIngredient> convertInput(ItemStack input, ItemStack filterItem) {
		List<ItemStack> list = new java.util.ArrayList<>(List.of(input, filterItem));
		return list.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList());
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return BTWEmiRecipeCategories.HOPPER;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}
	
	@Override
	public int getDisplayWidth() {
		return 132;
	}
	
	@Override
	public int getDisplayHeight() {
		return 70;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0, 132, 70, 0, 0);
		widgets.addSlot(inputs.get(0), 30, 8).appendTooltip(Text.translatable("Input"));
		widgets.addSlot(EmiStack.of(Block.hopperBlock), 56, 36).drawBack(false);
		widgets.addSlot(inputs.get(1), 56, 19).drawBack(false).appendTooltip(Text.translatable("Filter Item")).recipeContext(this);
		
		if (!(outputs.size() == 1)) {
			widgets.addSlot(outputs.get(0), 86, 45).appendTooltip(Text.translatable("Output")).recipeContext(this);
			widgets.addSlot(outputs.get(1), 86, 8).appendTooltip(Text.translatable("Filter output")).recipeContext(this);
			widgets.addTexture(BACKGROUND, 63, 46, 20, 13, 0, 73);
		}
		else {
			widgets.addSlot(outputs.get(0), 86, 8).appendTooltip(Text.translatable("Filter output")).recipeContext(this);
		}
	}
}
