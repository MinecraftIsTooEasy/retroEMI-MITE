package emi.mitemod.emi.mixin;

import emi.mitemod.emi.api.EMIResourceLocation;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceLocation.class)
public class ResourceLocationMixin implements EMIResourceLocation {

    @Shadow private final String resourceDomain;
    @Shadow private final String resourcePath;

    public ResourceLocationMixin(String resourceDomain, String resourcePath) {
        this.resourceDomain = resourceDomain;
        this.resourcePath = resourcePath;
    }

    @Override
    public int compareTo(ResourceLocation that) {
        int i = this.resourcePath.compareTo(that.getResourcePath());
        if (i != 0) return i;
        return this.resourceDomain.compareTo(that.getResourceDomain());
    }

    @Inject(method = "exists", at = @At("RETURN"), cancellable = true)
    public void exists(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
