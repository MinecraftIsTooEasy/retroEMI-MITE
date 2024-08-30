package dev.emi.emi.recipe;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import moddedmite.emi.api.EMIInventoryCrafting;
import moddedmite.emi.api.EMIShapedRecipes;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.IRecipe;
import net.minecraft.InventoryCrafting;
import net.minecraft.Material;
import net.minecraft.ShapedRecipes;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmiShapedRecipe extends EmiCraftingRecipe {

	private final ShapedRecipes shaped_recipe;
	private final int crafting_difficulty;

	public EmiShapedRecipe(ShapedRecipes recipe, int craftingDifficulty) {
		super(padIngredients((EMIShapedRecipes) recipe), EmiStack.of(EmiPort.getOutput(recipe)), new SyntheticIdentifier(recipe), false, ((EMIShapedRecipes) recipe).getSecondaryOutput(null));
        crafting_difficulty = craftingDifficulty;
        setRemainders(input, recipe);
		this.shaped_recipe = recipe;
	}

	@Override
	public Material craftLevel() {
		return this.shaped_recipe.getMaterialToCheckToolBenchHardnessAgainst();
	}

	public static void setRemainders(List<EmiIngredient> input, IRecipe recipe) {
		InventoryCrafting inv = EmiUtil.getCraftingInventory();
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).isEmpty()) {
				continue;
			}
			for (int j = 0; j < input.size(); j++) {
				if (j == i) {
					continue;
				}
				if (!input.get(j).isEmpty()) {
					inv.setInventorySlotContents(j, input.get(j).getEmiStacks().get(0).getItemStack().copy());
				}
			}
			List<EmiStack> stacks = input.get(i).getEmiStacks();
			for (EmiStack stack : stacks) {
				inv.setInventorySlotContents(i, stack.getItemStack().copy());
				if (stack.getItemStack().getItem().hasContainerItem()) {
					stack.setRemainder(EmiStack.of(stack.getItemStack().getItem().getContainerItem()));
				}
			}
			Arrays.fill(((EMIInventoryCrafting) inv).getStackList(), null);
		}
	}
	
	public static List<EmiIngredient> padIngredients(EMIShapedRecipes recipe) {
		return padIngredients(recipe.getRecipeWidth(), recipe.getRecipeHeight(),
				Arrays.stream(recipe.getRecipeItems()).map(RetroEMI::wildcardIngredient).collect(Collectors.toList()));
	}


	public static List<EmiIngredient> padIngredients(int width, int height, List<EmiIngredient> in) {
		List<EmiIngredient> list = Lists.newArrayList();
		int i = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (x >= width || y >= height || i >= in.size()) {
					list.add(EmiStack.EMPTY);
				}
				else {
					list.add(in.get(i++));
				}
			}
		}
		return list;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		float crafting_time = (float) ((Math.pow((crafting_difficulty - 100), 0.74) + 100) / 20);
		widgets.addText(EmiPort.translatable("emi.craft_difficult.items", String.format("%d", crafting_difficulty)), 55, 45, 0xFFFFFFFF, true);
		widgets.addText(EmiPort.translatable("emi.craft_time.items", String.format("%s", decimalFormat.format(crafting_time))), 55, 35, 0xFFFFFFFF, true);
	}
}
