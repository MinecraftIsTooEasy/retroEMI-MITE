package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.data.EmiRemoveFromIndex;
import emi.mitemod.emi.api.EMIBlock;
import emi.mitemod.emi.api.EMIItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Override
    public Item hideFromEMI() {
        if (FishModLoader.getEnvironmentType().equals(EnvType.CLIENT)) {
            for (int i = 0; i < 16; i++) {
                EmiRemoveFromIndex.removed.add(EmiStack.of(new ItemStack((Item) ReflectHelper.dyCast(this), 1, i)));
            }
        }
        return ReflectHelper.dyCast(this);
    }


    @Inject(method={"<clinit>"}, at={@At(value="TAIL")})
    private static void addToCreativeTab(CallbackInfo ci) {
//        ((EMIItem) Item.fragsCreeper).hideFromEMI();
//        ((EMIItem) Item.fragsInfernalCreeper).hideFromEMI();
//        ((EMIItem) Item.referencedBook).hideFromEMI();
//        ((EMIItem) Item.fragsNetherspawn).hideFromEMI();
//        ((EMIItem) Item.thrownWeb).hideFromEMI();
//        ((EMIItem) Item.genericFood).hideFromEMI();
    }
}
