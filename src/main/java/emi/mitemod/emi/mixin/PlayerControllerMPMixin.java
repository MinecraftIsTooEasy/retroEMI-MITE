package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMIPlayerControllerMP;
import net.minecraft.NetClientHandler;
import net.minecraft.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin implements EMIPlayerControllerMP {
    @Shadow private final NetClientHandler netClientHandler;

    public PlayerControllerMPMixin(NetClientHandler netClientHandler) {
        this.netClientHandler = netClientHandler;
    }

    public NetClientHandler getNetClientHandler() {
        return this.netClientHandler;
    }
}
