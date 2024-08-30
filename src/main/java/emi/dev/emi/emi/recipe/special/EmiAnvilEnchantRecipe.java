package emi.dev.emi.emi.recipe.special;

import java.util.Arrays;
import java.util.List;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.*;

public class EmiAnvilEnchantRecipe implements EmiRecipe {
    public static final List<Enchantment> ENCHANTMENTS = Arrays.stream(Enchantment.enchantmentsList)
            .filter(e -> e != null).toList();
    private final Item tool;
    private final Enchantment enchantment;
    private final int level;
    private final ResourceLocation id;

    public EmiAnvilEnchantRecipe(Item tool, Enchantment enchantment, int level, ResourceLocation id) {
        this.tool = tool;
        this.enchantment = enchantment;
        this.level = level;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(tool), getBook());
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(tool));
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
        widgets.addSlot(EmiStack.of(tool), 0, 0);
        widgets.addSlot(getBook(), 49, 0);
        widgets.addSlot(EmiStack.of(getTool()), 107, 0).recipeContext(this);
    }

    private ItemStack getTool() {
        ItemStack itemStack = new ItemStack(tool);
        itemStack.addEnchantment(enchantment, level);
        return itemStack;
    }

    private EmiStack getBook() {
        ItemStack item = new ItemStack(Item.enchantedBook);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList StoredEnchantments = new NBTTagList();
        NBTTagCompound enchant = new NBTTagCompound();
//        int id = ((NBTTagCompound)getTool().getTag().getTagList("ench").get(0)).getShort("id");

        enchant.getShort("id");
        enchant.getShort("lvl");
        StoredEnchantments.appendTag(enchant);
        tag.getShort("StoredEnchantments");
        item.getStoredEnchantmentTagList();
//        NBTTagCompound tag = new NBTTagCompound();
//        NBTTagList StoredEnchantments = new NBTTagList();
//        NBTTagCompound enchant = new NBTTagCompound();
//        int id = ((NBTTagCompound)getTool().getTagCompound().getTagList("ench").copy()).getShort("id");
//
//        enchant.setShort("id", (short)id);
//        enchant.setShort("lvl", (short) level);
//        StoredEnchantments.appendTag(enchant);
//        tag.setShort("StoredEnchantments", StoredEnchantments.getId());
//        item.readFromNBT(tag);
        return EmiStack.of(item);
    }
}

