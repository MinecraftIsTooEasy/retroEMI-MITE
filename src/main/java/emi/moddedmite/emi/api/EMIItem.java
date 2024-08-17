package emi.moddedmite.emi.api;

import net.minecraft.Item;
import net.minecraft.ItemStack;

public interface EMIItem {
    default boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
    default int getFurnaceBurnTime(int iItemDamage) {
        return 0;
    }

    default Item hideFromEMI() {
        return null;
    }

}
