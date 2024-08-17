package emi.moddedmite.emi.api;

import net.minecraft.ResourceLocation;

public interface EMIResourceLocation {
    default int compareTo(ResourceLocation that) {
        return 0;
    }
}
