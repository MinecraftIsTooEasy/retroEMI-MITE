package moddedmite.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.screen.tooltip.EmiSecondaryOutputComponent;
import moddedmite.emi.MITEPlugin;
import moddedmite.emi.api.recipe.MITEEmiRecipeCategories;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.util.SyntheticIdentifier;

import java.util.List;

public class EmiCompostingRecipe implements EmiRecipe {
    private final EmiIngredient input;
    private final List<EmiStack> remains;
    public final float compostValue;

    public EmiCompostingRecipe(ItemStack compostStack) {
        this.compostValue = compostStack.getItem().getCompostingValue();
        this.input = RetroEMI.wildcardIngredient(compostStack);
        Item remain = compostStack.getItem().getCompostingRemains(compostStack);

        if (remain != null)
            this.remains = List.of(EmiStack.of(remain));
        else
            this.remains = List.of();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MITEEmiRecipeCategories.COMPOSTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return new SyntheticIdentifier(this);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.remains;
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
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 28, 4);

        int arrowX = EmiTexture.FULL_ARROW.width;
        arrowX = (int) Math.min(arrowX * this.compostValue, arrowX);
        int arrowY = EmiTexture.FULL_ARROW.height;
        int uU = EmiTexture.FULL_ARROW.u;
        int uV = EmiTexture.FULL_ARROW.v;

        widgets.addTexture(EmiRenderHelper.WIDGETS, 28, 4, arrowX, arrowY, uU, uV)
                .tooltip(List.of(TooltipComponent.of(EmiPort.translatable("emi.compost_value.items", (int) (this.compostValue * 100)))));
        widgets.addSlot(input, 2, 3);
        widgets.addSlot(EmiStack.of(Item.manure)
                .setAmount(Math.max((long) Math.floor(compostValue), 1)), 62, 3);

        if (!this.remains.isEmpty()) {
            widgets.addTexture(MITEPlugin.SMALL_PLUS, 54, 3)
                    .tooltip(List.of(new EmiSecondaryOutputComponent(this.remains.stream().map(EmiStack::getItemStack).toArray(ItemStack[]::new))));
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
