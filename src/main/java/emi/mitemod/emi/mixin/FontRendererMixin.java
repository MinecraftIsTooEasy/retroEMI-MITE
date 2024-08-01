package emi.mitemod.emi.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FontRenderer.class)
public class FontRendererMixin {
    @ModifyConstant(method = {"<init>"}, constant = {@Constant(intValue = 256)})
    private int modifyChanceTableSize(int val) {
        return Short.MAX_VALUE;
    }
}
