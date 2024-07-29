package emi.shims.java.net.minecraft.item;


import emi.shims.java.net.minecraft.util.DyeColor;
import net.minecraft.Item;
import net.minecraft.ItemStack;

public record DyeItem(DyeColor color) {
	
	public ItemStack toStack() {
		return new ItemStack(Item.dyePowder, 1, color.ordinal());
	}
	
	public static DyeItem byColor(DyeColor color) {
		return new DyeItem(color);
	}
	
}
