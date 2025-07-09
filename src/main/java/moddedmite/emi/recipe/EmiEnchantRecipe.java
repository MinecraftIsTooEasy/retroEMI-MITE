package moddedmite.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import moddedmite.emi.api.recipe.MITEEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import shims.java.net.minecraft.util.SyntheticIdentifier;

import java.util.List;

public class EmiEnchantRecipe implements EmiRecipe {
    private final EmiStack enchantItem;
    private final EmiStack enchantedItem;
    private final int experienceCost;

    public EmiEnchantRecipe(EmiStack enchantItem, EmiStack enchantedItem, int experienceCost) {
        this.enchantItem = enchantItem;
        this.enchantedItem = enchantedItem;
        this.experienceCost = experienceCost;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MITEEmiRecipeCategories.ENCHANT;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return new SyntheticIdentifier(this);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(this.enchantItem.getEmiStacks()));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of((EmiStack) EmiIngredient.of(this.enchantItem.getEmiStacks()));
    }

    @Override
    public int getDisplayWidth() {
        return 72;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int r = 184 - (int) (120.0F);
        int g = 226 - (int) (66.0F);
        int b = 3 + (int) (29.0F);
        int color = (r << 16) + (g << 8) + b;
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 25, 12);
        widgets.addText(EmiPort.translatable("emi.exp_cost.items", String.format("%1d", experienceCost)), 25, 2, color, true);
        widgets.addSlot(enchantItem, 0, 12);
        widgets.addSlot(enchantedItem, 54, 12);
    }
}
