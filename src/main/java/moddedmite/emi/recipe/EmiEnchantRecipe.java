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
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
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
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 24;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 28, 4)
                .tooltip(List.of(TooltipComponent.of(EmiPort.translatable("emi.exp_cost.items", String.format("%1d", experienceCost)))));
        widgets.addSlot(enchantItem, 2, 3);
        widgets.addSlot(enchantedItem, 62, 3);
    }
}
