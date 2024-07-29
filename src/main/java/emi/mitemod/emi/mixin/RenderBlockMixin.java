package emi.mitemod.emi.mixin;

import net.minecraft.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;

//@Mixin(RenderBlocks.class)
public class RenderBlockMixin {

    public static boolean doesRenderIDRenderItemIn3D(int par0) {
        return par0 == 0 ? true : (par0 == 31 ? true : (par0 == 39 ? true : (par0 == 13 ? true : (par0 == 10 ? true : (par0 == 11 ? true : (par0 == 27 ? true : (par0 == 22 ? true : (par0 == 21 ? true : (par0 == 16 ? true : (par0 == 26 ? true : (par0 == 32 ? true : (par0 == 34 ? true : par0 == 35))))))))))));
    }
}
