package emi.dev.emi.emi.api.plugin;

import emi.dev.emi.emi.api.EmiEntrypoint;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.*;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.runtime.EmiReloadLog;
import emi.mitemod.emi.util.Color;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.*;
import emi.dev.emi.emi.recipe.mite.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

@EmiEntrypoint
public class MITEPlugin implements EmiPlugin {

	public static final ResourceLocation WIDGETS = new ResourceLocation("textures/recipe/btwwidgets.png");
	public static final EmiTexture SMALL_PLUS = new EmiTexture(WIDGETS, 36, 0, 7, 7);

	static {
		MITEEmiRecipeCategories.HOPPER = category("hopper", EmiStack.of(Block.hopperBlock));
		MITEEmiRecipeCategories.PISTON = category("piston", EmiStack.of(Block.pistonBase));
		MITEEmiRecipeCategories.FOOD = category("food", EmiStack.of(Item.carrot), Comparator.comparingInt(a -> ((EmiFoodRecipe) a).getHunger()));
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
		}
		catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing EMI recipe (no ID available)");
			EmiReloadLog.error(e);
		}
	}
	
	private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, IRecipe recipe) {
		try {
			registry.addRecipe(supplier.get());
		}
		catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing MITE recipe " + recipe);
			EmiReloadLog.error(e);
		}
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon) {
		return new EmiRecipeCategory(new ResourceLocation("mite", id), icon,
				new EmiTexture(new ResourceLocation("textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
		return new EmiRecipeCategory(new ResourceLocation("mite", id), icon,
				new EmiTexture(new ResourceLocation("textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void register(EmiRegistry reg) {
//		reg.addCategory(MITEEmiRecipeCategories.SOULFORGE);
//		reg.addCategory(MITEEmiRecipeCategories.CAULDRON);
//		reg.addCategory(MITEEmiRecipeCategories.CRUCIBLE);
//		reg.addCategory(MITEEmiRecipeCategories.HOPPER);
//		reg.addCategory(MITEEmiRecipeCategories.KILN);
//		reg.addCategory(MITEEmiRecipeCategories.MILLSTONE);
//		reg.addCategory(MITEEmiRecipeCategories.PISTON);
//		reg.addCategory(MITEEmiRecipeCategories.SAW);
//		reg.addCategory(MITEEmiRecipeCategories.TURNTABLE);
//		reg.addCategory(MITEEmiRecipeCategories.CAMPFIRE);
		reg.addCategory(MITEEmiRecipeCategories.FOOD);
//		reg.addCategory(MITEEmiRecipeCategories.TRADING);
//		reg.addCategory(MITEEmiRecipeCategories.PROGRESSIVE);
//
//		reg.addWorkstation(MITEEmiRecipeCategories.HOPPER, EmiStack.of(Block.hopperBlock));
//		reg.addWorkstation(MITEEmiRecipeCategories.KILN, EmiStack.of(Block.brick));
//		reg.addWorkstation(MITEEmiRecipeCategories.PISTON, EmiStack.of(Block.pistonBase));
//
//		//Foods
		for (Item it : Item.itemsList) { // There must be a better way to do this than iterating the registry... right?
			if (it != null && (it.getNutrition() > 0 || it.getSatiation(null) > 0)) {
				addRecipeSafe(reg, () -> new EmiFoodRecipe(new ItemStack(it)));
			}
		}
//
//		//Info
//
		addInfoRecipes(reg);
//		addWorldRecipes(reg);
	}

	private void addInfoRecipes(EmiRegistry registry) {

		//------ Early game info ------//
		info(registry, Block.gravel, 0, "mite.gravel.info");

		// Stone
//		info(registry, Block.stone, 0, "btw.stone.info");
//		info(registry, Block.stone, 1, "btw.deepslate.info");
//		info(registry, Block.stone, 2, "btw.blackstone.info");
//
//		info(registry, Block.stoneBrick, 1, "btw.mossy_stone_brick.info");
//		info(registry, Block.stoneBrick, 5, "btw.mossy_deepslate_brick.info");
//		info(registry, Block.stoneBrick, 9, "btw.mossy_blackstone_brick.info");
//
//		info(registry, Block.stoneBrick, 2, "btw.cracked_stone_brick.info");
//		info(registry, Block.stoneBrick, 6, "btw.cracked_deepslate_brick.info");
//		info(registry, Block.stoneBrick, 10, "btw.cracked_blackstone_brick.info");
//
//		info(registry, BTWItems.stoneBrick, 0, "btw.stone_brick_item.info");
//		info(registry, BTWItems.stoneBrick, 1, "btw.deepslate_brick_item.info");
//		info(registry, BTWItems.stoneBrick, 2, "btw.blackstone_brick_item.info");

		// General early game
//		info(registry, Block.anvil, "btw.anvil.info");
//		info(registry, Item.dyePowder, Color.WHITE.colorID, "btw.bonemeal.info");
//		info(registry, BTWBlocks.unlitCampfire, "btw.campfire.info");
//		info(registry, BTWItems.ironChisel, "btw.chisel.info");
//		info(registry, BTWItems.diamondChisel, "btw.chisel.info");
//		info(registry, BTWBlocks.web, "btw.cobweb.info");
//		info(registry, Item.fishingRod, "btw.fishing_rod.info");
//		info(registry, BTWItems.baitedFishingRod, "btw.fishing_rod.info");
//		info(registry, Block.netherrack, "btw.netherrack.info");
//		info(registry, BTWBlocks.idleOven, "btw.oven.info");
//		info(registry, Item.silk, "btw.string.info");
//		info(registry, BTWBlocks.finiteUnlitTorch, "btw.torch.info");
//		info(registry, BTWBlocks.finiteBurningTorch, "btw.torch.info");


		//------ Tech info ------//

		// Enchanting/magic
		HashSet<Integer> enchantmentSet = new HashSet<>();
		for (int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
			Enchantment enchantment = Enchantment.get(i);
			if (enchantment != null && !enchantmentSet.contains(enchantment.effectId)) {
				enchantmentSet.add(enchantment.effectId);
				info(registry, Item.enchantedBook.getEnchantedItemStack(
						new EnchantmentData(enchantment, enchantment.getNumLevels())),
						"mite.enchanted_book.info." + i);
			}
		}
//		info(registry, Block.brewingStand, "btw.brewing_stand.info");
//		info(registry, Block.enchantmentTable, "btw.enchanter.info");
//		info(registry, BTWBlocks.infernalEnchanter, "btw.infernal_enchanter.info");

		// Beacon info
//		info(registry, Block.beacon, "btw.beacon.info");
//		info(registry, BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_HELLFIRE, "btw.concentrated_hellfire_block.info");
//		info(registry, Block.blockDiamond, "btw.diamond_block.info");
//		info(registry, BTWBlocks.aestheticEarth, AestheticOpaqueEarthBlock.SUBTYPE_DUNG, "btw.dung_block.info");
//		info(registry, Block.blockEmerald, "btw.emerald_block.info");
//		info(registry, BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK, "btw.ender_block.info");
//		info(registry, Block.glass, "btw.glass.info");
//		info(registry, Block.glowStone, "btw.glowstone.info");
//		info(registry, Block.blockGold, "btw.gold_block.info");
//		info(registry, Block.blockIron, "btw.iron_block.info");
//		info(registry, Block.blockLapis, "btw.lapis_block.info");
//		info(registry, BTWBlocks.spiderEyeBlock, "btw.spider_eye_block.info");
//		info(registry, BTWBlocks.soulforgedSteelBlock, "btw.steel_block.info");

		//------ Misc info ------//

//		info(registry, BTWBlocks.aestheticVegetation, AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING, "btw.blood_wood.info");
//		info(registry, BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_CLEAN, "btw.chopping_block.info");
//		info(registry, BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY, "btw.chopping_block.info");
//		info(registry, BTWBlocks.companionCube, "btw.companion_cube.info");
//		info(registry, BTWItems.corpseEye, "btw.corpse_eye.info");
//		info(registry, BTWItems.netherGrothSpores, "btw.groth.info");
//		info(registry, Block.portal, "btw.portal.info");
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
