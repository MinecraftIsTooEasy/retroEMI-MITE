package moddedmite.emi.mixin.recipe;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipesMITE.class)
public class RecipesMITEMixin {
    @Redirect(
            method = "addCraftingRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/CraftingManager;addShapelessRecipe(Lnet/minecraft/ItemStack;Z[Ljava/lang/Object;)Lnet/minecraft/ShapelessRecipes;",
                    ordinal = 8
            )
    )
    private static ShapelessRecipes removeRepeatedRecipe(CraftingManager instance, ItemStack var8, boolean var7, Object[] var11) {
        return null;
    }

    @Redirect(
            method = "addCraftingRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/ShapelessRecipes;setSkillset(I)V",
                    ordinal = 10
            )
    )
    private static void removeRepeatedRecipe(ShapelessRecipes instance, int skillset) {
    }

    @Inject(method = "addCraftingRecipes", at = @At("RETURN"))
    private static void removeRepeatedRecipe(CraftingManager crafting_manager, CallbackInfo ci) {
        for (int i = 2; i < 4; ++i) {
            crafting_manager.addShapelessRecipe(new ItemStack(Item.cookie, i * 4), false, new ItemStack(Item.dough, i), new ItemStack(Item.chocolate, i)).setSkillset(Skill.FOOD_PREPARATION.id);
        }
    }
}
