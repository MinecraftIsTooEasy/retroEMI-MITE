package emi.dev.emi.emi.recipe.special;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.shims.java.net.minecraft.item.DyeItem;
import emi.shims.java.net.minecraft.item.DyeableItem;
import emi.shims.java.net.minecraft.util.DyeColor;
import net.minecraft.Item;
import net.minecraft.ItemArmor;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmiArmorDyeRecipe extends EmiPatternCraftingRecipe {
	public static final List<Item> DYEABLE_ITEMS = Arrays.stream(Item.itemsList).filter(Objects::nonNull)
			.filter(i -> i instanceof ItemArmor a).collect(Collectors.toList());
	private static final List<DyeItem> DYES = Stream.of(DyeColor.values()).map(c -> DyeItem.byColor(c)).collect(Collectors.toList());
	private final Item armor;
	
	public EmiArmorDyeRecipe(Item armor, ResourceLocation id) {
		super(List.of(EmiIngredient.of(DYES.stream().map(i -> (EmiIngredient) EmiStack.of(i)).collect(Collectors.toList())), EmiStack.of(armor)),
				EmiStack.of(armor), id);
		this.armor = armor;
	}
	
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new SlotWidget(EmiStack.of(armor), x, y);
		}
		else {
			final int s = slot - 1;
			return new GeneratedSlotWidget(r -> {
				List<DyeItem> dyes = getDyes(r);
				if (s < dyes.size()) {
					return EmiStack.of(dyes.get(s));
				}
				return EmiStack.EMPTY;
			}, unique, x, y);
		}
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(r -> {
			return EmiStack.of(DyeableItem.blendAndSetColor(new ItemStack(armor), getDyes(r)));
		}, unique, x, y);
	}
	
	private List<DyeItem> getDyes(Random random) {
		List<DyeItem> dyes = Lists.newArrayList();
		int amount = 1 + random.nextInt(8);
		for (int i = 0; i < amount; i++) {
			dyes.add(DYES.get(random.nextInt(DYES.size())));
		}
		return dyes;
	}
}
