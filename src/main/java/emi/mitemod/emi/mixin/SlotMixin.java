package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMISlot;
import net.minecraft.IInventory;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin implements EMISlot {
    @Shadow private int slotIndex;

    @Shadow protected boolean locked;

    @Override
    public int getSlotIndex() {
        return this.slotIndex;
    }

    @Inject(method = "<init>(Lnet/minecraft/IInventory;IIIZ)V", at = @At("RETURN"))
    public void setUnLocked(IInventory inventory, int slot_index, int display_x, int display_y, boolean accepts_large_items, CallbackInfo ci) {
        this.locked = false;
    }

    @Inject(method = "setLocked", at = @At("HEAD"), cancellable = true)
    public void setLocked(boolean locked, CallbackInfo info) {
        if (locked) {
            info.cancel();
        }
    }
}
