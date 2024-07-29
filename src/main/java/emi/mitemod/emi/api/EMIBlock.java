package emi.mitemod.emi.api;

import net.minecraft.Block;

public interface EMIBlock {
    default Block hideFromEMI() {
        return null;
    }

    default Block hideFromEMI(int metadata) {
        return null;
    }
}
