package emi.dev.emi.emi.recipe.btw;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmiSawRecipe implements EmiRecipe {
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/recipe/btwwidgets.png");
	public static final EmiTexture SAW = new EmiTexture(BACKGROUND, 20, 0, 16, 5);
	public static final EmiTexture SAWBASE = new EmiTexture(BACKGROUND, 20, 10, 16, 9);
	private final ResourceLocation id;
	private final List<EmiStack> output;
	private final List<EmiIngredient> input;
	
	public EmiSawRecipe(ResourceLocation id, Block input, int[] metadata, ItemStack[] output) {
		this.id = id;
		this.input = convertInput(
				List.of(new ItemStack(input, 1, metadata[0])).stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList()));
		this.output = convertOutput(output);
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return BTWEmiRecipeCategories.SAW;
	}
	
	@Override
	public @Nullable ResourceLocation getId() {
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
	public int getDisplayWidth() {
		return 110;
	}
	
	@Override
	public int getDisplayHeight() {
		return 37;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(input.get(0), 0, 0);
		
		for (int i = 0; i < Math.max(output.size(), 6); i++) {
			if (i < output.size()) {
				widgets.addSlot(output.get(i), (i % 3 * 18) + 55, ((i / 3) * 18)).recipeContext(this);
			}
			else {
				widgets.addSlot(EmiStack.of(ItemStacks.EMPTY), (i % 3 * 18) + 55, ((i / 3) * 18)).recipeContext(this);
			}
		}
		
		widgets.addCustomAnimatedTexture(SAW, 4, 20, 1, 2);
		widgets.addFillingArrow(24, 10, 25 * 200);
		widgets.addTexture(SAWBASE, 1, 25);
	}
	
	private List<EmiStack> convertOutput(ItemStack[] stacks) {
		List<EmiStack> list = new ArrayList<>(Collections.emptyList());
		for (ItemStack stack : stacks) {
			list.add(EmiStack.of(stack));
		}
		return list;
	}
	
	public static List<EmiIngredient> convertInput(List<EmiIngredient> input) {
		List<EmiIngredient> list = Lists.newArrayList();
		
		int i = 0;
		for (int y = 0; y < input.size(); y++) {
			list.add(input.get(i));
			i++;
		}
		return list;
	}
}
