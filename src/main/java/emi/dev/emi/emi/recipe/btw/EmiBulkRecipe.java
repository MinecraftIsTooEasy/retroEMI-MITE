package emi.dev.emi.emi.recipe.btw;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public class EmiBulkRecipe implements EmiRecipe {
	private final ResourceLocation id;
	private final EmiRecipeCategory category;
	private final List<EmiIngredient> input;
	private final List<EmiStack> output;
	private final boolean stoked;
	
	public EmiBulkRecipe(ResourceLocation id, List<ItemStack> input, List<ItemStack> output, boolean stoked, EmiRecipeCategory category) {
		this.id = id;
		this.category = category;
		this.input = padIngredients(input.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList()));
		this.output = convertOutput(output);
		this.stoked = stoked;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return category;
	}
	
	@Override
	public ResourceLocation getId() {
		return id;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}
	
	@Override
	public List<EmiIngredient> getCatalysts() {
		return stoked ? List.of(EmiIngredient.of(List.of(EmiStack.of(Block.furnaceBurning)))) : List.of();
	}
	
	@Override
	public int getDisplayWidth() {
		return 132;
	}
	
	@Override
	public int getDisplayHeight() {
		return 52;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addFillingArrow(55, 5, 50 * 200);
		widgets.addTexture(EmiTexture.EMPTY_FLAME, 60, 26); //TODO make custom fire texture for stoked instead of text
		widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 60, 26, 1000 * 20, false, true, true);
		if (stoked) {
			widgets.addText(EmiPort.ordered(EmiPort.translatable("emi.btw.bulk.stoked")), 1, 42, -1, true);
		}
		for (int i = 0; i < Math.max(input.size(), 6); i++) {
			if (i < input.size()) {
				widgets.addSlot(input.get(i), (i % 3 * 18), 4 + ((i / 3) * 18));
			}
			else {
				widgets.addSlot(EmiStack.of(ItemStacks.EMPTY), (i % 3 * 18), 4 + ((i / 3) * 18));
			}
		}
		for (int i = 0; i < output.size(); i++) {
			widgets.addSlot(output.get(i), 80 + (i % 2 * 26), i / 2 * 26).large(true).recipeContext(this);
		}
	}
	
	
	public static List<EmiIngredient> padIngredients(List<EmiIngredient> input) {
		List<EmiIngredient> list = Lists.newArrayList();
		
		int i = 0;
		for (int y = 0; y < input.size(); y++) {
			list.add(input.get(i));
			i++;
		}
		return list;
	}
	
	
	public static List<EmiStack> convertOutput(List<ItemStack> output) {
		List<EmiStack> list = Lists.newArrayList();
		for (ItemStack stack : output) {
			list.add(EmiStack.of(stack));
		}
		return list;
	}
}
