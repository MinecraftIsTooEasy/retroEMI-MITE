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
import shims.java.net.minecraft.text.Text;
import net.minecraft.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.emi.emi.VanillaPlugin.basicWorld;
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

		{ //hidden stacks
			{ //blocks
				registry.removeEmiStacks(EmiStack.of(Block.waterMoving));
				registry.removeEmiStacks(EmiStack.of(Block.waterStill));
				registry.removeEmiStacks(EmiStack.of(Block.lavaMoving));
				registry.removeEmiStacks(EmiStack.of(Block.lavaStill));
				registry.removeEmiStacks(EmiStack.of(Block.pistonExtension));
				registry.removeEmiStacks(EmiStack.of(Block.pistonMoving));
				registry.removeEmiStacks(EmiStack.of(Block.stoneDoubleSlab));
				registry.removeEmiStacks(EmiStack.of(Block.bed));
				registry.removeEmiStacks(EmiStack.of(Block.mushroomBrown));
				registry.removeEmiStacks(EmiStack.of(Block.mushroomRed));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneWire));
				registry.removeEmiStacks(EmiStack.of(Block.crops));
				registry.removeEmiStacks(EmiStack.of(Block.tilledField));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceBurning));
				registry.removeEmiStacks(EmiStack.of(Block.signPost));
				registry.removeEmiStacks(EmiStack.of(Block.doorWood));
				registry.removeEmiStacks(EmiStack.of(Block.signWall));
				registry.removeEmiStacks(EmiStack.of(Block.doorIron));
				registry.removeEmiStacks(EmiStack.of(Block.oreRedstoneGlowing));
				registry.removeEmiStacks(EmiStack.of(Block.torchRedstoneIdle));
				registry.removeEmiStacks(EmiStack.of(Block.reed));
				registry.removeEmiStacks(EmiStack.of(Block.cake));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneRepeaterIdle));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneRepeaterActive));
				registry.removeEmiStacks(EmiStack.of(Block.pumpkinStem));
				registry.removeEmiStacks(EmiStack.of(Block.melonStem));
				registry.removeEmiStacks(EmiStack.of(Block.mushroomCapBrown));
				registry.removeEmiStacks(EmiStack.of(Block.mushroomCapRed));
				registry.removeEmiStacks(EmiStack.of(Block.endPortal));
				registry.removeEmiStacks(EmiStack.of(Block.brewingStand));
				registry.removeEmiStacks(EmiStack.of(Block.cauldron));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneLampIdle));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneLampActive));
				registry.removeEmiStacks(EmiStack.of(Block.woodDoubleSlab));
				registry.removeEmiStacks(EmiStack.of(Block.carrot));
				registry.removeEmiStacks(EmiStack.of(Block.potato));
				registry.removeEmiStacks(EmiStack.of(Block.skull));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneComparatorActive));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceClayBurning));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceSandstoneBurning));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceObsidianBurning));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceNetherrackBurning));
				registry.removeEmiStacks(EmiStack.of(Block.obsidianDoubleSlab));
				registry.removeEmiStacks(EmiStack.of(Block.onions));
				registry.removeEmiStacks(EmiStack.of(Block.cropsDead));
				registry.removeEmiStacks(EmiStack.of(Block.carrotDead));
				registry.removeEmiStacks(EmiStack.of(Block.potatoDead));
				registry.removeEmiStacks(EmiStack.of(Block.onionsDead));
				registry.removeEmiStacks(EmiStack.of(Block.flowerPotMulti));
				registry.removeEmiStacks(EmiStack.of(Block.bush));
				registry.removeEmiStacks(EmiStack.of(Block.furnaceHardenedClayBurning));
				registry.removeEmiStacks(EmiStack.of(Block.netherStalk));
				registry.removeEmiStacks(EmiStack.of(Block.tripWire));
				registry.removeEmiStacks(EmiStack.of(Block.cocoaPlant));
				registry.removeEmiStacks(EmiStack.of(Block.flowerPot));
				registry.removeEmiStacks(EmiStack.of(Block.redstoneComparatorIdle));
				registry.removeEmiStacks(EmiStack.of(Block.doorCopper));
				registry.removeEmiStacks(EmiStack.of(Block.doorSilver));
				registry.removeEmiStacks(EmiStack.of(Block.doorGold));
				registry.removeEmiStacks(EmiStack.of(Block.doorAncientMetal));
				registry.removeEmiStacks(EmiStack.of(Block.doorMithril));
				registry.removeEmiStacks(EmiStack.of(Block.doorAdamantium));
				registry.removeEmiStacks(EmiStack.of(Block.spark));
				registry.removeEmiStacks(EmiStack.of(Block.portal));
			}
			{ //items
				registry.removeEmiStacks(EmiStack.of(Item.fragsCreeper));
				registry.removeEmiStacks(EmiStack.of(Item.fragsInfernalCreeper));
				registry.removeEmiStacks(EmiStack.of(Item.referencedBook));
				registry.removeEmiStacks(EmiStack.of(Item.fragsNetherspawn));
				registry.removeEmiStacks(EmiStack.of(Item.thrownWeb));
				registry.removeEmiStacks(EmiStack.of(Item.genericFood));
			}
		}
	}

	private void addInfoRecipes(EmiRegistry registry) {

		info(registry, Block.gravel, 0, "mite.gravel.info");

		// Enchantment
		Arrays.stream(Enchantment.enchantmentsList)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(
						enchantment -> enchantment.effectId,
						enchantment -> enchantment,
						(existing, replacement) -> existing
				))
				.values()
				.forEach(enchantment ->
						info(registry,
								Item.enchantedBook.getEnchantedItemStack(
										new EnchantmentData(enchantment, enchantment.getNumLevels())),
								"enchanted_book.info." + enchantment.getName())
				);
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
		for (Item item : Item.itemsList) {
			if (item instanceof ItemMattock itemMattock)
				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(Block.dirt), EmiStack.of(itemMattock), EmiStack.of(Block.tilledField), new ResourceLocation("mite", item + "/tilling")));
			if (item instanceof ItemMeat meat && !meat.is_cooked && meat != Item.rottenFlesh)
				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(meat), EmiStack.of(Block.fire), EmiStack.of(meat.getCookedItem()), new ResourceLocation("mite", item + String.valueOf(item.itemID) + "/barbecue")));
		}
	}
}
