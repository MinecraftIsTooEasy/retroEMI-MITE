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
import emi.dev.emi.emi.recipe.btw.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

@EmiEntrypoint
public class BTWPlugin implements EmiPlugin {
	
	public static final ResourceLocation WIDGETS = new ResourceLocation("emi", "textures/recipe/btwwidgets.png");
	public static final EmiTexture SMALL_PLUS = new EmiTexture(WIDGETS, 36, 0, 7, 7);
	
	static {
		BTWEmiRecipeCategories.HOPPER = category("hopper", EmiStack.of(Block.hopperBlock));
		BTWEmiRecipeCategories.PISTON = category("piston", EmiStack.of(Block.pistonBase));
		BTWEmiRecipeCategories.FOOD = category("food", EmiStack.of(Item.carrot), Comparator.comparingInt(a -> ((EmiFoodRecipe) a).getHunger()));
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
			EmiReloadLog.warn("Exception when parsing BTW recipe " + recipe);
			EmiReloadLog.error(e);
		}
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon) {
		return new EmiRecipeCategory(new ResourceLocation("btw", id), icon,
				new EmiTexture(new ResourceLocation("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
	}
	
	public static EmiRecipeCategory category(String id, EmiStack icon, Comparator<EmiRecipe> comp) {
		return new EmiRecipeCategory(new ResourceLocation("btw", id), icon,
				new EmiTexture(new ResourceLocation("emi", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16), comp);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void register(EmiRegistry reg) {
		reg.addCategory(BTWEmiRecipeCategories.SOULFORGE);
		reg.addCategory(BTWEmiRecipeCategories.CAULDRON);
		reg.addCategory(BTWEmiRecipeCategories.CRUCIBLE);
		reg.addCategory(BTWEmiRecipeCategories.HOPPER);
		reg.addCategory(BTWEmiRecipeCategories.KILN);
		reg.addCategory(BTWEmiRecipeCategories.MILLSTONE);
		reg.addCategory(BTWEmiRecipeCategories.PISTON);
		reg.addCategory(BTWEmiRecipeCategories.SAW);
		reg.addCategory(BTWEmiRecipeCategories.TURNTABLE);
		reg.addCategory(BTWEmiRecipeCategories.CAMPFIRE);
		reg.addCategory(BTWEmiRecipeCategories.FOOD);
		reg.addCategory(BTWEmiRecipeCategories.TRADING);
		reg.addCategory(BTWEmiRecipeCategories.PROGRESSIVE);

		reg.addWorkstation(BTWEmiRecipeCategories.HOPPER, EmiStack.of(Block.hopperBlock));
		reg.addWorkstation(BTWEmiRecipeCategories.KILN, EmiStack.of(Block.brick));
		reg.addWorkstation(BTWEmiRecipeCategories.PISTON, EmiStack.of(Block.pistonBase));

		//Foods
		for (Item it : Item.itemsList) { // There must be a better way to do this than iterating the registry... right?
			if (it instanceof ItemFood food) {
				List<ItemStack> foodList = new ArrayList<>();
				food.getSubItems(food.itemID, food.getCreativeTab(), foodList);
				for (ItemStack stack : foodList) {
					addRecipeSafe(reg, () -> new EmiFoodRecipe(stack));
				}
			}
		}

		//Info
		
		addInfoRecipes(reg);
		addWorldRecipes(reg);
	}
	
	private void addInfoRecipes(EmiRegistry registry) {
		
		//------ Early game info ------//
		
		// Stone
		info(registry, Block.stone, 0, "btw.stone.info");
		info(registry, Block.stone, 1, "btw.deepslate.info");
		info(registry, Block.stone, 2, "btw.blackstone.info");
		
		info(registry, Block.stoneBrick, 1, "btw.mossy_stone_brick.info");
		info(registry, Block.stoneBrick, 5, "btw.mossy_deepslate_brick.info");
		info(registry, Block.stoneBrick, 9, "btw.mossy_blackstone_brick.info");
		
		info(registry, Block.stoneBrick, 2, "btw.cracked_stone_brick.info");
		info(registry, Block.stoneBrick, 6, "btw.cracked_deepslate_brick.info");
		info(registry, Block.stoneBrick, 10, "btw.cracked_blackstone_brick.info");

		
		// General early game
		info(registry, Block.anvil, "btw.anvil.info");
		info(registry, Item.dyePowder, Color.WHITE.colorID, "btw.bonemeal.info");
		info(registry, Item.fishingRodFlint, "btw.fishing_rod.info");
		info(registry, Block.netherrack, "btw.netherrack.info");
		info(registry, Block.furnaceBurning, "btw.furnace.info");
		info(registry, Item.silk, "btw.string.info");
		
		// Mortaring
		info(registry, Item.clay, "btw.mortar.info");
		info(registry, Item.slimeBall, "btw.mortar.info");

		
		//------ Tech info ------//

		
		// Devices
		info(registry, Block.brick, "btw.kiln.info");
		
		// Hopper
		info(registry, Block.fenceIron, "btw.iron_bars.info");
		info(registry, Block.trapdoor, "btw.trapdoor.info");
		info(registry, Block.slowSand, "btw.soul_sand.info");
		
		// Enchanting/magic
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				info(registry, Item.enchantedBook, Enchantment.enchantmentsList[i].effectId, "btw.enchantedBook.info");
			}
		}
		info(registry, Block.brewingStand, "btw.brewing_stand.info");
		info(registry, Block.enchantmentTable, "btw.enchanter.info");

		// Beacon info
		info(registry, Block.beacon, "btw.beacon.info");
		info(registry, Block.blockDiamond, "btw.diamond_block.info");
		info(registry, Block.blockEmerald, "btw.emerald_block.info");
		info(registry, Block.glass, "btw.glass.info");
		info(registry, Block.glowStone, "btw.glowstone.info");
		info(registry, Block.blockGold, "btw.gold_block.info");
		info(registry, Block.blockIron, "btw.iron_block.info");
		info(registry, Block.blockLapis, "btw.lapis_block.info");
		
		//------ Misc info ------//
		info(registry, Block.portal, "btw.portal.info");
	}
	
	private void info(EmiRegistry registry, Item item, String info) {
		registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(item)), List.of(Text.translatable(info)), null));
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
