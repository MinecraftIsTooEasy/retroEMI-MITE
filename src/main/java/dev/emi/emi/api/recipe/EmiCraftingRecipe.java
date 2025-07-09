package dev.emi.emi.api.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.screen.tooltip.EmiSecondaryOutputComponent;
import moddedmite.emi.MITEPlugin;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.Block;
import net.minecraft.Material;
import shims.java.com.unascribed.retroemi.ItemStacks;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import shims.java.net.minecraft.text.MutableText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmiCraftingRecipe implements EmiRecipe {
	protected final ResourceLocation id;
	protected final List<EmiIngredient> input;
	protected final EmiStack output;
	public final boolean shapeless;
	public final ItemStack[] secondaryOutputs;
	private final float crafting_difficulty;

	public EmiCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id, ItemStack[] secondaryOutputs, float craftingDifficulty) {
		this(input, output, id, true, secondaryOutputs, craftingDifficulty);
	}
	
	public EmiCraftingRecipe(List<EmiIngredient> input, EmiStack output, ResourceLocation id, boolean shapeless, ItemStack[] secondaryOutputs, float craftingDifficulty) {
		this.input = input;
		this.output = output;
		this.id = id;
        this.shapeless = shapeless;
		this.secondaryOutputs = secondaryOutputs;
		this.crafting_difficulty = craftingDifficulty;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.CRAFTING;
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
		List<EmiStack> list = new ArrayList<>(output.getEmiStacks());
		if (secondaryOutputs != null) {
			list.addAll(Arrays.stream(secondaryOutputs).map(EmiStack::of).toList());
		}
		return list;
	}
	
	@Override
	public int getDisplayWidth() {
		return 118;
	}
	
	@Override
	public int getDisplayHeight() {
		return 54;
	}
	
	public boolean canFit(int width, int height) {
		if (input.size() > 9) {
			return false;
		}
		for (int i = 0; i < input.size(); i++) {
			int x = i % 3;
			int y = i / 3;
			if (!input.get(i).isEmpty() && (x >= width || y >= height)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
		if (shapeless) {
			widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
		}
		int sOff = 0;
		if (!shapeless) {
			if (canFit(1, 3)) {
				sOff -= 1;
			}
			if (canFit(3, 1)) {
				sOff -= 3;
			}
		}
		for (int i = 0; i < 9; i++) {
			int s = i + sOff;
			if (s >= 0 && s < input.size()) {
				widgets.addSlot(input.get(s), i % 3 * 18, i / 3 * 18);
			} else {
				widgets.addSlot(EmiStack.of(ItemStacks.EMPTY), i % 3 * 18, i / 3 * 18);
			}
		}
		if (secondaryOutputs != null) {
			widgets.addTexture(MITEPlugin.SMALL_PLUS, 84, 23).tooltip(List.of(new EmiSecondaryOutputComponent(secondaryOutputs)));
		}
		widgets.addSlot(output, 92, 14).large(true).recipeContext(this);

		if (EmiConfig.MITECraftInfo) {
			DecimalFormat decimalFormat = new DecimalFormat("#.#");
			float crafting_time = (float) ((Math.pow((crafting_difficulty - 100), 0.74) + 100) / 20);
			widgets.addText(EmiPort.translatable("emi.craft_difficult.items", String.format("%s", decimalFormat.format(crafting_difficulty))), 55, 45, -1, true);
			widgets.addText(EmiPort.translatable("emi.craft_time.items", String.format("%s", decimalFormat.format(crafting_time))), 55, 35, -1, true);
			if (!Block.workbench.isValidMetadata(2) || this.getCraftLevel() == null || this.getCraftLevel() == Material.air || this.getCraftLevel() == Material.rusted_iron || this.getCraftLevel() == Material.wood) return;
			MutableText materialName = EmiPort.literal(this.getCraftLevel().getLocalizedName());
			widgets.addText(EmiPort.translatable("emi.craft_level.items", materialName), 55, 0, -1, true);
		}
	}
}
