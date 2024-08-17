package emi.moddedmite.emi.api;

import net.minecraft.IInventory;
import net.minecraft.ItemStack;

public interface EMIShapedRecipes {
    default int getRecipeHeight() {
        return 0;
    }
    default int getRecipeWidth() {
        return 0;
    }
    default ItemStack[] getRecipeItems() {
        return null;
    }
    default ItemStack[] getSecondaryOutput(IInventory inventory) {
        return null;
    }
}
