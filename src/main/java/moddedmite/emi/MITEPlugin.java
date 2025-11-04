package moddedmite.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.config.EmiConfig;
import moddedmite.emi.recipe.EmiCompostingRecipe;
import moddedmite.emi.recipe.EmiEnchantRecipe;
import moddedmite.emi.recipe.EmiFoodRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import moddedmite.emi.api.recipe.MITEEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import moddedmite.emi.util.EnchantmentNameIDTranslator;
import shims.java.net.minecraft.text.Text;
import net.minecraft.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import static dev.emi.emi.api.recipe.VanillaEmiRecipeCategories.SMELTING;

@EmiEntrypoint
public class MITEPlugin implements EmiPlugin {
	public static final EmiTexture SMALL_PLUS = new EmiTexture(EmiRenderHelper.WIDGETS, 111, 0, 7, 7);

	static {
		MITEEmiRecipeCategories.ENCHANT = category("enchant", EmiStack.of(Block.enchantmentTable));
		MITEEmiRecipeCategories.FOOD = category("food", EmiStack.of(Item.carrot), Comparator.comparingInt(a -> ((EmiFoodRecipe) a).getNutrition()));
		MITEEmiRecipeCategories.COMPOSTING = category("composting", EmiStack.of(Item.wormRaw),
				Comparator.comparingDouble(value -> ((EmiCompostingRecipe) value).compostValue));
//		Comparator<EmiRecipe> tradeComparitor = Comparator.comparingInt(a -> ((EmiTradeRecipe) a).professionId); //Silly generics, tricks are for kids
//		MITEEmiRecipeCategories.TRADING = category("trading", EmiStack.of(Item.emerald), tradeComparitor.thenComparingInt(a -> {
//			int level = ((EmiTradeRecipe) a).tradeLevel;
//			return ((EmiTradeRecipe) a).isLevelUp ? -level : level;
//		}));
	}
	
	//Executed in the EMIPlugin instead of here as iterating through all recipes *twice* isn't ideal. Only here for clarity.
	public static void addCustomIRecipes(IRecipe recipe, EmiRegistry registry) {
	}
	
	private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
		try {
			registry.addRecipe(supplier.get());
		} catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing EMI recipe (no ID available)", e);
		}
	}
	
	private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, IRecipe recipe) {
		try {
			registry.addRecipe(supplier.get());
		} catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing MITE recipe " + recipe, e);
		}
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon) {
		return new EmiRecipeCategory(new ResourceLocation("MITE", id), icon,
				new EmiTexture(new ResourceLocation("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
		return new EmiRecipeCategory(new ResourceLocation("MITE", id), icon,
				new EmiTexture(new ResourceLocation("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void register(EmiRegistry registry) {
		if (EmiConfig.moreWorkstation) {
			registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceClayIdle));
			registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceHardenedClayIdle));
			registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceSandstoneIdle));
			registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceObsidianIdle));
			registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceNetherrackIdle));
		}

		registry.addCategory(MITEEmiRecipeCategories.FOOD);
		registry.addCategory(MITEEmiRecipeCategories.ENCHANT);
		registry.addCategory(MITEEmiRecipeCategories.COMPOSTING);
//		registry.addCategory(MITEEmiRecipeCategories.TRADING);

		// Foods and compost
		for (Item it : Item.itemsList) { // There must be a better way to do this than iterating the registry... right?
			// Null check
			if (it == null) {
				continue;
			}

			// Food
			if (it.getNutrition() > 0 || it.getSatiation(null) > 0) {
				addRecipeSafe(registry, () -> new EmiFoodRecipe(new ItemStack(it)));
			}

			// Compost
			if (it.getCompostingValue() > 0F) {
				ItemStack recipeStack;

				// Handle recipe stack
				if (it.getHasSubtypes())
					recipeStack = new ItemStack(it, 1, Short.MAX_VALUE);
				else
					recipeStack = new ItemStack(it);

				addRecipeSafe(registry, () -> new EmiCompostingRecipe(recipeStack));
			}
		}

		addRecipeSafe(registry, () -> new EmiEnchantRecipe(EmiStack.of(new ItemStack(Item.appleGold, 1, 0)), EmiStack.of(new ItemStack(Item.appleGold, 1, 1)), 200));
		addRecipeSafe(registry, () -> new EmiEnchantRecipe(EmiStack.of(new ItemStack(Item.potion, 1, 0)), EmiStack.of(new ItemStack(Item.expBottle, 1, 1)), 200));

		addInfoRecipes(registry);
		addWorldRecipes(registry);
	}

	private void addInfoRecipes(EmiRegistry registry) {

		info(registry, Block.gravel, 0, "mite.gravel.info");

		// Enchantment
		HashSet<Integer> enchantmentSet = new HashSet<>();
		for (int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
			Enchantment enchantment = EnchantmentNameIDTranslator.getEnchantmentByText(i);
			if (enchantment != null && !enchantmentSet.contains(enchantment.effectId)) {
				enchantmentSet.add(enchantment.effectId);
				info(registry, Item.enchantedBook.getEnchantedItemStack(
						new EnchantmentData(enchantment, enchantment.getNumLevels())),
						"enchanted_book.info." + enchantment.getName());
			}
		}
	}
	
	private void info(EmiRegistry registry, Item item, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(item)), List.of(Text.translatable(info)), null));
	}

	private void info(EmiRegistry registry, ItemStack stack, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(stack)), List.of(Text.translatable(info)), null));
	}
	
	private void info(EmiRegistry registry, Item item, int metadata, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(new ItemStack(item, 1, metadata))), List.of(Text.translatable(info)), null));
	}
	
	private void info(EmiRegistry registry, Block block, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(block)), List.of(Text.translatable(info)), null));
	}
	
	private void info(EmiRegistry registry, Block block, int metadata, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(new ItemStack(block, 1, metadata))), List.of(Text.translatable(info)), null));
	}
	
	private void addWorldRecipes(EmiRegistry registry) {
		List<ItemStack> woodTypes = new ArrayList<>();
//		Block.wood.getSubBlocks(Block.wood.blockID, Block.wood.getCreativeTabToDisplayOn(), woodTypes);
//		registry.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("emi", "/world/block_interaction/btw/crafting_stump"))
//				.leftInput(EmiStack.of(Item.ironChisel)).rightInput(EmiIngredient.of(woodTypes.stream().map(EmiStack::of).toList()), false, (sw) -> {
//					sw.appendTooltip(Text.translatable("emi.world_interaction.btw.crafting_stump"));
//					return sw;
//				}).output(EmiStack.of(Block.workbench)).supportsRecipeTree(false).build());

	}
}
