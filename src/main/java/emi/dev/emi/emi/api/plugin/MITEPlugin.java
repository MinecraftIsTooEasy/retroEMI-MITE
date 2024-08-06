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
			if (it instanceof ItemFood food) {
				List<ItemStack> foodList = new ArrayList<>();
				food.getSubItems(food.itemID, food.getCreativeTab(), foodList);
				for (ItemStack stack : foodList) {
					addRecipeSafe(reg, () -> new EmiFoodRecipe(stack));
				}
			}
		}
//
//		//Info
//
//		addInfoRecipes(reg);
//		addWorldRecipes(reg);
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
