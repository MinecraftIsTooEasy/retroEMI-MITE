package moddedmite.emi.mixin.client;

import moddedmite.emi.api.EMIInventoryCrafting;
import net.minecraft.IInventory;
import net.minecraft.InventoryCrafting;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryCrafting.class)
public abstract class InventoryCraftingMixin implements EMIInventoryCrafting, IInventory {
    @Shadow private int inventoryWidth;
    @Shadow private ItemStack[] stackList;

    @Override
    public int getInventoryWidth() {
        return this.inventoryWidth;
    }

    @Override
    public ItemStack[] getStackList() {
        return this.stackList;
    }
}
