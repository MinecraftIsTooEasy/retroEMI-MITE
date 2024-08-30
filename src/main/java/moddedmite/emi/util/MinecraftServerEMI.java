package moddedmite.emi.util;

import net.minecraft.server.MinecraftServer;

import java.io.File;

public abstract class MinecraftServerEMI extends MinecraftServer {

    public static boolean isServer = false;

    public MinecraftServerEMI(File par1File) {
        super(par1File);
    }

    public static boolean getIsServer() {
        return isServer;
    }
}
