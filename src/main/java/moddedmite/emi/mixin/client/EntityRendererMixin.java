package moddedmite.emi.mixin.client;

import net.minecraft.EntityRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityRenderer;disableLightmap(D)V"))
    private void preventGuiDark(float par1, CallbackInfo ci) {
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
    }
}
