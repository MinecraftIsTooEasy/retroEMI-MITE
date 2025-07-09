package dev.emi.emi.recipe.special;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.Item;
import net.minecraft.ResourceLocation;

import java.util.List;
import java.util.Random;

public class EmiMapCloningRecipe extends EmiPatternCraftingRecipe {
	
	public EmiMapCloningRecipe(ResourceLocation id) {
		super(List.of(EmiStack.of(Item.map), EmiStack.of(Item.emptyMap)), EmiStack.of(Item.map), id);
	}
	
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new SlotWidget(EmiStack.of(Item.map), x, y);
		} else {
			final int s = slot - 1;
			return new GeneratedSlotWidget(r -> {
				int amount = r.nextInt(8) + 1;
				if (s < amount) {
					return EmiStack.of(Item.emptyMap);
				}
				return EmiStack.EMPTY;
			}, unique, x, y);
		}
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(r -> EmiStack.of(Item.map, r.nextInt(8) + 2), unique, x, y);
	}
	
	public EmiStack getAmount(Random random, Item item) {
		return EmiStack.of(item);
	}
}
