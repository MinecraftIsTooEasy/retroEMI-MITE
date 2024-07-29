package emi.shims.java.com.unascribed.retroemi;


import net.minecraft.ItemStack;

public class ItemStacks {
	
	public static final ItemStack EMPTY = null;
	
	public static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.stackSize == 0 || stack.itemID == 0;
	}
	
}
