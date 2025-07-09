package moddedmite.emi.mixin.client;

import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shims.java.com.unascribed.retroemi.REMIMixinHooks;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    @Shadow private int[] charWidth;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modifyChanceTableSize(GameSettings par1GameSettings, ResourceLocation par2ResourceLocation, TextureManager par3TextureManager, boolean par4, CallbackInfo ci) {
        if (FishModLoader.hasMod("better_game_setting")) return;
        this.charWidth = new int[32767];
    }

    @ModifyVariable(
            method = "renderStringAtPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            ordinal = 0 //var3
    )
    private int modifyVar3(int original, String text, boolean shadow) {
        if (FishModLoader.hasMod("better_game_setting")) return original;
        return REMIMixinHooks.applyCustomFormatCodes(ReflectHelper.dyCast(this), text, shadow, original);
    }
}
