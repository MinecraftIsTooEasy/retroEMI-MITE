package moddedmite.emi.api;

import net.minecraft.IInventory;
import net.minecraft.ItemStack;

import java.util.List;

public interface EMIShapelessRecipes {
    default List getRecipeItems() {
        return null;
    }
    default ItemStack[] getSecondaryOutput(IInventory inventory) {
        return null;
    }
}
