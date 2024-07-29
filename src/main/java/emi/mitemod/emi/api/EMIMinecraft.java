package emi.mitemod.emi.api;

import net.minecraft.Timer;

public interface EMIMinecraft {
    default Timer getTimer() {
        return null;
    }
}
