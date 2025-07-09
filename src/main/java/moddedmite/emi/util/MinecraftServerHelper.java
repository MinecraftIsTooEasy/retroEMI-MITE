package moddedmite.emi.util;

import net.minecraft.server.MinecraftServer;

import java.io.File;

public abstract class MinecraftServerHelper extends MinecraftServer {
    public static boolean isServer = false;

    public MinecraftServerHelper(File par1File) {
        super(par1File);
    }

    public static boolean isServer() {
        return isServer;
    }
}
