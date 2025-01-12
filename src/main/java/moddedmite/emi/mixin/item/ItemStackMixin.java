package moddedmite.emi.mixin.item;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.data.EmiRemoveFromIndex;
import moddedmite.emi.api.EMIItemStack;
import net.fabricmc.api.EnvType;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public class ItemStackMixin implements EMIItemStack {
    @Shadow public int damage;
    @Shadow public int itemID;
    @Shadow public NBTTagCompound stackTagCompound;

    @Override
    public boolean isItemEqual(ItemStack par1ItemStack) {
        return this.itemID == par1ItemStack.itemID && this.damage == par1ItemStack.getItemDamage();
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

    @Override
    public void setEnchanted() {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        if (!this.stackTagCompound.hasKey("ench")) {
            this.stackTagCompound.setTag("ench", new NBTTagList("ench"));
        }
    }

    @Shadow
    public ItemStack setTagCompound(NBTTagCompound nbtTagCompound) {
        return null;
    }
}
