package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMIItem;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements EMIItem {
    @Unique
    protected int defaultFurnaceBurnTime = 0;

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
    @Override
    public int getFurnaceBurnTime(int iItemDamage) {
        return defaultFurnaceBurnTime;
    }
}
