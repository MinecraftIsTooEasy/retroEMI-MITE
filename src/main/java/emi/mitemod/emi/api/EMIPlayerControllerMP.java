package emi.mitemod.emi.api;

import net.minecraft.NetClientHandler;

public interface EMIPlayerControllerMP {
    default NetClientHandler getNetClientHandler() {
        return null;
    }
}
