package moddedmite.emi.util;

import net.minecraft.ItemStack;
import net.minecraft.TileEntityFurnace;

public class TileEntityFurnaceHelper extends TileEntityFurnace {
    public static boolean isItemFuelS(ItemStack item_stack) {
        return item_stack.getItem().getHeatLevel(item_stack) > 0;
    }
}
