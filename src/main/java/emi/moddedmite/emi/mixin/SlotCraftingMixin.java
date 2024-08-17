package emi.moddedmite.emi.mixin;

import emi.dev.emi.emi.Hooks;
import emi.moddedmite.emi.api.EMISlotCrafting;
import net.minecraft.EntityPlayer;
import net.minecraft.IInventory;
import net.minecraft.ItemStack;
import net.minecraft.SlotCrafting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class SlotCraftingMixin implements EMISlotCrafting {

    @Final
    @Shadow private IInventory craftMatrix;
    @Shadow private EntityPlayer thePlayer;

    public IInventory getCraftMatrix() {
        return this.craftMatrix;
    }
    @Inject(method = "onCrafting(Lnet/minecraft/ItemStack;)V", at = @At("HEAD"))
    private void onCraftRenderEMI(ItemStack par1ItemStack, CallbackInfo ci) {
        Hooks.onCrafting(this.thePlayer, this.craftMatrix);
    }
}
