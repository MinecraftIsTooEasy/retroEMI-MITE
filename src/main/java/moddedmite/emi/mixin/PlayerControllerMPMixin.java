package moddedmite.emi.mixin;

import moddedmite.emi.api.EMIPlayerControllerMP;
import net.minecraft.NetClientHandler;
import net.minecraft.PlayerControllerMP;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin implements EMIPlayerControllerMP {
    @Mutable
    @Final
    @Shadow
    protected final NetClientHandler netClientHandler;

    public PlayerControllerMPMixin(NetClientHandler netClientHandler) {
        this.netClientHandler = netClientHandler;
    }

    public NetClientHandler getNetClientHandler() {
        return this.netClientHandler;
    }
}
