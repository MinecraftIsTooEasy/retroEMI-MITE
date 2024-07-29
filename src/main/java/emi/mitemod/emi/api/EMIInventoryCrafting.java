package emi.mitemod.emi.api;

import net.minecraft.ItemStack;

public interface EMIInventoryCrafting {
    default int getInventoryWidth() {
        return 0;
    }
    default ItemStack[] getStackList() {
        return null;
    }
}
