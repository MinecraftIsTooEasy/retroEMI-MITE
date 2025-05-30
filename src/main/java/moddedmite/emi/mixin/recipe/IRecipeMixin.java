package moddedmite.emi.mixin.recipe;

import dev.emi.emi.data.EmiData;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.IRecipe;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IRecipe.class)
public interface IRecipeMixin {
    default IRecipe hideFromEMI() {
        EmiData.hideRecipe(new SyntheticIdentifier(ReflectHelper.dyCast(this)));
        return ReflectHelper.dyCast(this);
    }
}
