package emi.mitemod.emi.api;

import net.minecraft.ItemStack;

public interface EMIItem {
    default boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
    default int getFurnaceBurnTime(int iItemDamage) {
        return 0;
    }
}
