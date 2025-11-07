package moddedmite.emi.api;

import net.minecraft.Item;
import net.minecraft.ItemStack;

public interface EMIItemStack {
    default boolean isItemEqual(ItemStack par1ItemStack) {
        return false;
    }

    @Deprecated(since = "1.1.24")
    default Item hideFromEMI() {
        return null;
    }

    default void setEnchanted() {}
}
