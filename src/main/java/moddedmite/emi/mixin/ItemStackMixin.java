package moddedmite.emi.mixin;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.data.EmiRemoveFromIndex;
import moddedmite.emi.api.EMIItemStack;
import net.fabricmc.api.EnvType;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EMIItemStack {
    @Shadow public int itemID;
    @Unique
    private int itemDamage;

    @Override
    public boolean isItemEqual(ItemStack par1ItemStack) {
        return this.itemID == par1ItemStack.itemID && this.itemDamage == this.itemDamage;
    }

    @Override
    public Item hideFromEMI() {
        if (FishModLoader.getEnvironmentType().equals(EnvType.CLIENT)) {
            for (int i = 0; i < 16; i++) {
                EmiRemoveFromIndex.removed.add(EmiStack.of(new ItemStack((Block) ReflectHelper.dyCast(this), 1, i)));
            }
        }
        return ReflectHelper.dyCast(this);
    }

}
