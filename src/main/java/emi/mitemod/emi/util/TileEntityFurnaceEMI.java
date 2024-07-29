package emi.mitemod.emi.util;

import net.minecraft.ItemStack;
import net.minecraft.TileEntityFurnace;

//@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceEMI extends TileEntityFurnace{
    public static boolean isItemFuel0(ItemStack item_stack) {
        return item_stack.getItem().getHeatLevel(item_stack) > 0;
    }
}
