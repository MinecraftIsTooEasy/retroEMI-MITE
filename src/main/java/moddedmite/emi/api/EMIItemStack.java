package moddedmite.emi.api;

import net.minecraft.Item;
import net.minecraft.ItemStack;

public interface EMIItemStack {
    default boolean isItemEqual(ItemStack par1ItemStack) {
        return false;
    }
    default Item hideFromEMI() {
        return null;
    }
    default public void setEnchanted() {}
}
