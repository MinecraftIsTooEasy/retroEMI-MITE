package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.screen.EmiScreenManager;
import emi.mitemod.emi.api.EMIGuiContainerCreative;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.class)
public abstract class GuiContainerCreativeMixin extends InventoryEffectRenderer {

    public GuiContainerCreativeMixin(Container par1Container) {
        super(par1Container);
    }


    @Inject(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/CreativeTabs;getTabIndex()I", shift = At.Shift.AFTER))
    protected void keyTyped(char par1, int par2, CallbackInfo ci) {
        if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat) && !EmiScreenManager.search.isFocused()) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
        }
    }

    @Shadow
    private void setCurrentCreativeTab(CreativeTabs tabAllSearch) {
    }
}
