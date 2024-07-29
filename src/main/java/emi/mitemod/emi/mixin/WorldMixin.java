package emi.mitemod.emi.mixin;

import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        RetroEMI.tick();
    }
}
