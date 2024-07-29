package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMISlot;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public class SlotMixin implements EMISlot {
    @Shadow private int slotIndex;

    @Override
    public int getSlotIndex() {
        return this.slotIndex;
    }
}
