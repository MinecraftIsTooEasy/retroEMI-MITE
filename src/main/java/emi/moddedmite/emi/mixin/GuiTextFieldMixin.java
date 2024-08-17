package emi.moddedmite.emi.mixin;

import emi.moddedmite.emi.api.EMIGuiTextField;
import net.minecraft.Gui;
import net.minecraft.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiTextField.class)
public class GuiTextFieldMixin extends Gui implements EMIGuiTextField {
    @Shadow private boolean isEnabled = true;

    @Override
    public boolean getIsEnabled() {
        return this.isEnabled;
    }
}
