package moddedmite.emi.mixin.client;

import net.minecraft.Block;
import net.minecraft.Minecraft;
import net.minecraft.RenderBlocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlocks.class)
public class RenderBlockMixin {

    @Shadow @Final private Minecraft minecraftRB;

    @Inject(method = "setRenderBoundsForNonStandardFormBlock", at = @At("HEAD"))
    public void pause(Block block, CallbackInfo ci) {
        if (this.minecraftRB == null) {
            int x = 0;
            x ++;
        }
    }

}
