package emi.mitemod.emi.api;

import net.minecraft.IInventory;

public interface EMISlotCrafting {
    default IInventory getCraftMatrix() {
        return null;
    }
}
