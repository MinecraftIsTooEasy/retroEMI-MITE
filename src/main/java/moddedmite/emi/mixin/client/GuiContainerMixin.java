package moddedmite.emi.mixin.client;

import dev.emi.emi.Hooks;
import dev.emi.emi.screen.EmiScreenManager;
import moddedmite.emi.api.EMIGuiContainerCreative;
import moddedmite.emi.api.EMISearchInput;
import net.minecraft.Container;
import net.minecraft.GuiContainer;
import net.minecraft.GuiScreen;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin extends GuiScreen implements EMIGuiContainerCreative {
    @Shadow public int xSize;
    @Shadow public int ySize;
    @Shadow public int guiLeft;
    @Shadow public int guiTop;
    @Shadow public Slot theSlot;
    @Shadow public Container inventorySlots;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void addEMIWidgets(CallbackInfo ci) {
        EmiScreenManager.addWidgets(this);
    }

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/GuiContainer;drawGuiContainerBackgroundLayer(FII)V",
                    shift = At.Shift.AFTER
            ))
    private void renderEMIBackground(int par1, int par2, float par3, CallbackInfo ci) {
        Hooks.renderBackground(par1, par2); //render EMI background
    }

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/GuiContainer;drawGuiContainerForegroundLayer(II)V",
                    shift = At.Shift.AFTER
            ))
    private void renderForegroundPost(int par1, int par2, float par3, CallbackInfo ci) {
        Hooks.renderForegroundPre(par1, par2, this.mc);
        Hooks.renderForegroundPost(par1, par2, this.mc);
    }

    @Inject(method = "drawSlotInventory", at = @At(value = "RETURN"))
    private void drawSlot(Slot par1Slot, CallbackInfo ci) {
        Hooks.drawSlot(par1Slot);
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    public void disableHotkeyInEMISearchInput(char par1, int par2, CallbackInfo ci) {
        if (((EMISearchInput) this).getEMISearchInput()) {
            ci.cancel();
        }
    }

    @Override
    public Slot getTheSlot() {
        return this.theSlot;
    }

    @Override
    public int getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int getGuiTop() {
        return guiTop;
    }

    @Override
    public int getxSize() {
        return xSize;
    }

    @Override
    public int getySize() {
        return ySize;
    }
}
