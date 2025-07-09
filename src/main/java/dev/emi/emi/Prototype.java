package dev.emi.emi;

import net.minecraft.Item;
import net.minecraft.ItemStack;

public record Prototype(Item item, int meta) {

	public static final Prototype EMPTY = new Prototype(null);

	public Prototype(Item item) {
		this(item, 0);
	}

	public ItemStack toStack() {
		return toStack(1);
	}

	public ItemStack toStack(int count) {
		if (item == null) return null;
		return new ItemStack(item, count, meta);
	}
	
	public Item getItem() {
		return item();
	}

	public String toString() {
		if (item == null) return "Prototype[EMPTY]";
		return "Prototype[" + item.getItemDisplayName(new ItemStack(item)) + "(" + item.itemID + "):" + meta + "]";
	}


	public static Prototype of(ItemStack stack) {
		if (stack == null) return EMPTY;
		return new Prototype(stack.getItem(), stack.getItemSubtype());
	}

	public int hashCode() {
		return 10;
	}
}
