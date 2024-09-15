package moddedmite.emi.recipe.special;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;

import moddedmite.emi.api.EMIItemStack;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

public class EmiAnvilDisenchantRecipe
        implements EmiRecipe {
    private final Item tool;
    private final ResourceLocation id;
    private final int uniq = EmiUtil.RANDOM.nextInt();

    public EmiAnvilDisenchantRecipe(Item tool, ResourceLocation id) {
        this.tool = tool;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(this.tool), EmiStack.of(Item.bottleOfDisenchanting));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(this.tool));
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        int notUniq = this.uniq;
        widgets.addGeneratedSlot(r -> this.getItem(0), notUniq, 0, 0);
        widgets.addGeneratedSlot(r -> this.getItem(1), notUniq, 49, 0);
        widgets.addGeneratedSlot(r -> this.getItem(2), notUniq, 107, 0).recipeContext(this);
    }

    protected EmiStack getItem(int item) {
        ArrayList<ItemStack> items = Lists.newArrayList();
        items.add(this.getTool());
        items.add(Item.bottleOfDisenchanting.getItemStackForStatsIcon());
        items.add(this.getToolDisenchantm());
        return EmiStack.of((ItemStack)items.get(item));
    }

    private ItemStack getTool() {
        ItemStack stack = new ItemStack(this.tool);
        ((EMIItemStack) stack).setEnchanted();
        return stack;
    }

    private ItemStack getToolDisenchantm() {
        ItemStack stack = new ItemStack(this.tool);
        stack.clearEnchantTagList();
        return stack;
    }
}