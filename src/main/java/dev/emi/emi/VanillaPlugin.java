package dev.emi.emi;

import com.google.common.collect.Sets;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.stack.serializer.ListEmiIngredientSerializer;
import moddedmite.emi.MITEPlugin;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.config.EffectLocation;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.handler.CraftingRecipeHandler;
import dev.emi.emi.handler.InventoryRecipeHandler;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.recipe.*;
import dev.emi.emi.recipe.special.*;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
import dev.emi.emi.stack.serializer.TagEmiIngredientSerializer;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.*;
import moddedmite.emi.api.EMIGuiContainerCreative;
import moddedmite.emi.api.EMIShapelessRecipes;
import moddedmite.emi.recipe.special.EmiAnvilDisenchantRecipe;
import shims.java.com.unascribed.retroemi.PredicateAsSet;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.tag.TagKey;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.emi.emi.api.recipe.VanillaEmiRecipeCategories.*;

@EmiEntrypoint
public class VanillaPlugin implements EmiPlugin {
	public static EmiRecipeCategory TAG = new EmiRecipeCategory(new ResourceLocation("emi:tag"),
			EmiStack.of(Item.nameTag), simplifiedRenderer(240, 208), EmiRecipeSorting.none());

	public static EmiRecipeCategory INGREDIENT = new EmiRecipeCategory(new ResourceLocation("emi:ingredient"),
			EmiStack.of(Item.compass), simplifiedRenderer(240, 208));
	public static EmiRecipeCategory RESOLUTION = new EmiRecipeCategory(new ResourceLocation("emi:resolution"),
			EmiStack.of(Item.compass), simplifiedRenderer(240, 208));
	
	static {
		CRAFTING = new EmiRecipeCategory(new ResourceLocation("minecraft:crafting"),
				EmiStack.of(new ItemStack(Block.workbench, 1, 7)), simplifiedRenderer(240, 240), EmiRecipeSorting.compareOutputThenInput());
		SMELTING = new EmiRecipeCategory(new ResourceLocation("minecraft:smelting"),
				EmiStack.of(Block.furnaceIdle), simplifiedRenderer(224, 240), EmiRecipeSorting.compareOutputThenInput());
		ANVIL_REPAIRING = new EmiRecipeCategory(new ResourceLocation("emi:anvil_repairing"),
				EmiStack.of(Block.anvil), simplifiedRenderer(240, 224), EmiRecipeSorting.none());
		BREWING = new EmiRecipeCategory(new ResourceLocation("minecraft:brewing"),
				EmiStack.of(Item.brewingStand), simplifiedRenderer(224, 224), EmiRecipeSorting.none());
		WORLD_INTERACTION = new EmiRecipeCategory(new ResourceLocation("emi:world_interaction"),
				EmiStack.of(Item.itemsList[Block.grass.blockID]), simplifiedRenderer(208, 224), EmiRecipeSorting.none());
		EmiRenderable flame = (matrices, x, y, delta) -> {
			EmiTexture.FULL_FLAME.render(matrices, x + 1, y + 1, delta);
		};
		FUEL = new EmiRecipeCategory(new ResourceLocation("emi:fuel"), flame, flame, EmiRecipeSorting.compareInputThenOutput());
		INFO = new EmiRecipeCategory(new ResourceLocation("emi:info"),
				EmiStack.of(Item.writableBook), simplifiedRenderer(208, 224), EmiRecipeSorting.none());
	}


	@Override
	public void initialize(EmiInitRegistry registry) {
		registry.addIngredientSerializer(ItemEmiStack.class, new ItemEmiStackSerializer());
//		registry.addIngredientSerializer(FluidEmiStack.class, new FluidEmiStackSerializer());
		registry.addIngredientSerializer(TagEmiIngredient.class, new TagEmiIngredientSerializer());
		registry.addIngredientSerializer(ListEmiIngredient.class, new ListEmiIngredientSerializer());

//		registry.addRegistryAdapter(EmiRegistryAdapter.simple(Item.class, EmiPort.getItemRegistry(), EmiStack::of));
//		registry.addRegistryAdapter(EmiRegistryAdapter.simple(Fluid.class, EmiPort.getFluidRegistry(), EmiStack::of));
	}
	
	@Override
	public void register(EmiRegistry registry) {
		registry.addIngredientSerializer(ItemEmiStack.class, new ItemEmiStackSerializer());
		registry.addIngredientSerializer(TagEmiIngredient.class, new TagEmiIngredientSerializer());
		registry.addCategory(CRAFTING);
		registry.addCategory(SMELTING);
		registry.addCategory(ANVIL_REPAIRING);
		registry.addCategory(BREWING);
		registry.addCategory(WORLD_INTERACTION);
		registry.addCategory(FUEL);
		registry.addCategory(INFO);
		registry.addCategory(TAG);
		registry.addCategory(INGREDIENT);
		registry.addCategory(RESOLUTION);

		if (EmiConfig.moreWorkstation) {
			for (int i = 4; i < 11; i++) {
				registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, 0)));
				registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, 12)));
				registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, i)));
			}

			for (Block block : Block.blocksList) {
				if (block instanceof BlockAnvil anvil)
					registry.addWorkstation(ANVIL_REPAIRING, EmiStack.of(anvil));
			}
		} else {
			registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, 7)));
			registry.addWorkstation(ANVIL_REPAIRING, EmiStack.of(new ItemStack(Block.anvil, 1, 0)));
		}

		registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceIdle));
		registry.addWorkstation(BREWING, EmiStack.of(Item.brewingStand));
		registry.addWorkstation(WORLD_INTERACTION, EmiStack.of(Block.grass));

		registry.addRecipeHandler(ContainerPlayer.class, new InventoryRecipeHandler());
		registry.addRecipeHandler(ContainerWorkbench.class, new CraftingRecipeHandler());
		registry.addRecipeHandler(ContainerFurnace.class, new CookingRecipeHandler<>(SMELTING));

		registry.addExclusionArea(GuiContainerCreative.class, (screen, consumer) -> {
			int left = ((EMIGuiContainerCreative) screen).getGuiLeft();
			int top = ((EMIGuiContainerCreative) screen).getGuiTop();
			int width = ((EMIGuiContainerCreative) screen).getxSize();
			int bottom = top + ((EMIGuiContainerCreative) screen).getySize();
			consumer.accept(new Bounds(left, top - 28, width, 28));
			consumer.accept(new Bounds(left, bottom, width, 28));
		});

		registry.addGenericExclusionArea((screen, consumer) -> {
			if (screen instanceof InventoryEffectRenderer inv) {
				Minecraft client = Minecraft.getMinecraft();
				Collection collection = client.thePlayer.getActivePotionEffects();
				int size = collection.size();
				if (client.thePlayer.isMalnourished()) ++size;
				if (client.thePlayer.isInsulinResistant()) ++size;
				if (client.thePlayer.is_cursed) ++size;
				if (size > 0) {
					int k = 33;
					if (size > 5) {
						k = 132 / (size - 1);
					}
					int right = ((EMIGuiContainerCreative) inv).getGuiLeft() + ((EMIGuiContainerCreative) inv).getxSize() + 2;
					int rightWidth = inv.width - right;
					if (rightWidth >= 32) {
						int top = ((EMIGuiContainerCreative) inv).getGuiTop();
						int height = (size - 1) * k + 32;
						int left, width;
						if (EmiConfig.effectLocation == EffectLocation.TOP) {
							top = ((EMIGuiContainerCreative) inv).getGuiTop() - 34;
							if (screen instanceof GuiContainerCreative) {
								top -= 28;
								if (EmiAgnos.isForge() || EmiAgnos.isModLoaded("rusted_iron_core")) {
									top -= 22;
								}
							}
							int xOff = 34;
							if (size == 1) {
								xOff = 122;
							} else if (size > 5) {
								xOff = (((EMIGuiContainerCreative) inv).getxSize() - 32) / (size - 1);
							}
							width = Math.max(122, (size - 1) * xOff + 32);
							left = ((EMIGuiContainerCreative) inv).getGuiLeft() + (((EMIGuiContainerCreative) inv).getxSize() - width) / 2;
							height = 32;
						} else {
							left = switch (EmiConfig.effectLocation) {
								case LEFT_COMPRESSED -> ((EMIGuiContainerCreative) inv).getGuiLeft() - 2 - 32;
								case LEFT -> ((EMIGuiContainerCreative) inv).getGuiLeft() - 2 - 120;
								default -> right;
							};
							width = switch (EmiConfig.effectLocation) {
								case LEFT, RIGHT -> 120;
								case LEFT_COMPRESSED, RIGHT_COMPRESSED -> 32;
								default -> 32;
							};
						}
						consumer.accept(new Bounds(left, top, width, height));
					}
				}
			}
		});

		Comparison potionComparison = Comparison.of((a, b) -> RetroEMI.getEffects(a).equals(RetroEMI.getEffects(b)));

		registry.setDefaultComparison(Item.potion, potionComparison);
		registry.setDefaultComparison(Item.enchantedBook, Comparison.compareNbt());
		var prev = EmiStack.of(Item.enchantedBook);
		for (var ench : Enchantment.enchantmentsList) {
			if (ench == null) continue;
			var book = new ItemStack(Item.enchantedBook);
			EnchantmentHelper.setEnchantments(Map.of(ench.effectId, ench.getNumLevels()), book);
			registry.addEmiStackAfter(prev = EmiStack.of(book), prev);
		}

		PredicateAsSet<Item> hiddenItems = i -> {
			for (var inv : EmiStackList.invalidators) {
				if (inv.test(EmiStack.of(i))) {
					return true;
				}
			}
			return false;
		};

		// This is hardcoded in CraftingManager in 1.6
		for (Item i : EmiRepairItemRecipe.TOOLS) {
			if (!hiddenItems.contains(i)) {
				addRecipeSafe(registry, () -> new EmiRepairItemRecipe(i, synthetic("crafting/repairing", EmiUtil.subId(i))));
			}
		}

		for (IRecipe recipe : (List<IRecipe>) registry.getRecipeManager().getRecipeList()) {
			if (recipe instanceof RecipesMapExtending map) {
				EmiStack paper = EmiStack.of(Item.paper);
				addRecipeSafe(registry, () -> new EmiCraftingRecipe(List.of(
						paper, paper, paper, paper,
						EmiStack.of(Item.map),
						paper, paper, paper, paper
				),
						EmiStack.of(Item.map),
						new ResourceLocation("minecraft", "map_extending"), false, null, recipe.getUnmodifiedDifficulty()), recipe);
			} else if (recipe instanceof ShapedRecipes shaped) {
				addRecipeSafe(registry, () -> new EmiShapedRecipe(shaped, (int) shaped.getUnmodifiedDifficulty()), recipe);
			} else if (recipe instanceof ShapelessRecipes shapeless) {
				addRecipeSafe(registry, () -> new EmiShapelessRecipe((EMIShapelessRecipes) shapeless, shapeless, shapeless.getUnmodifiedDifficulty()), recipe);
			} else if (recipe instanceof RecipesArmorDyes dye) {
				for (Item i : EmiArmorDyeRecipe.DYEABLE_ITEMS) {
					if (!hiddenItems.contains(i)) {
						addRecipeSafe(registry, () -> new EmiArmorDyeRecipe(i, synthetic("crafting/dying", EmiUtil.subId(i))), recipe);
					}
				}
			} else if (recipe instanceof RecipeFireworks fwork) {
				// All firework recipes are one recipe in 1.6
				addRecipeSafe(registry, () -> new EmiFireworkStarRecipe(new ResourceLocation("minecraft", "firework_star")), recipe);
				addRecipeSafe(registry, () -> new EmiFireworkStarFadeRecipe(new ResourceLocation("minecraft", "firework_star_fade")), recipe);
				addRecipeSafe(registry, () -> new EmiFireworkRocketRecipe(new ResourceLocation("minecraft", "firework_rocket")), recipe);
			} else if (recipe instanceof RecipesMapCloning map) {
				addRecipeSafe(registry, () -> new EmiMapCloningRecipe(new ResourceLocation("minecraft", "map_cloning")), recipe);
			} else {
				MITEPlugin.addCustomIRecipes(recipe, registry);
				// No way to introspect arbitrary recipes in 1.6. :(
			}
		}

		for (var recipe : ((Map<Integer, ItemStack>) FurnaceRecipes.smelting().getSmeltingList()).entrySet()) {
			int id = recipe.getKey();
			ItemStack in = new ItemStack(Item.itemsList[id]);
			ItemStack out = recipe.getValue();
			TileEntityFurnace furnace = new TileEntityFurnace();
			int fuel = furnace.getFuelHeatLevel();
			addRecipeSafe(registry, () -> new EmiCookingRecipe(new ResourceLocation("minecraft", "furnace/" + id), in, out, SMELTING, fuel, out.getExperienceReward()));
		}

		for (Item i : Item.itemsList) {
			if (i == null) continue;
			if (hiddenItems.contains(i)) {
				continue;
			}
			if (i.isRepairable()) {
				if (i instanceof ItemArmor ai && ai.getArmorMaterial() != null && ai.getArmorMaterial().getMaterialMobility() != 0) {
					var material = Item.itemsList[ai.getArmorMaterial().getMaterialMobility()];
					addRecipeSafe(registry, () -> new EmiAnvilRecipe(EmiStack.of(i), EmiStack.of(material),
							new ResourceLocation("minecraft", "anvil/armor/" + SyntheticIdentifier.describe(i) + "/" + SyntheticIdentifier.describe(material))));
				} else if (i instanceof ItemTool ti && ti.getToolMaterial().getMaterialMobility() != 0) {
					var material = Item.itemsList[ti.getToolMaterial().getMaterialMobility()];
					addRecipeSafe(registry, () -> new EmiAnvilRecipe(EmiStack.of(i), EmiStack.of(material),
							new ResourceLocation("minecraft", "anvil/tool/" + SyntheticIdentifier.describe(i) + "/" + SyntheticIdentifier.describe(material))));
				}
			}
			if (i.isDamageable() && !(i instanceof ItemAnvilBlock)) {
				addRecipeSafe(registry, () -> new EmiAnvilRepairToolRecipe(i, new ResourceLocation("minecraft", "anvil/repair/tool/" + SyntheticIdentifier.describe(i))));
				if (!(i instanceof ItemFishingRod) && !(i instanceof ItemCarrotOnAStick) && !(i instanceof ItemFlintAndSteel))
					addRecipeSafe(registry, () -> new EmiAnvilRepairItemRecipe(i, new ResourceLocation("minecraft", "anvil/repair/nugget/" + SyntheticIdentifier.describe(i))));
			}
			var is = new ItemStack(i);
			if (is.isEnchantable() && !(is.getItem() instanceof ItemAppleGold) && !(is.getItem() instanceof ItemBook) && !(is.getItem() instanceof ItemExpBottle) && !(is.getItem() instanceof ItemCarrotOnAStick)) {
				for (Enchantment e : EmiAnvilEnchantRecipe.ENCHANTMENTS) {
					if (e.canEnchantItem(is.getItem())) {
						int max = e.getNumLevels();
						int min = e.getLevel(is);
						while (min <= max) {
							int finalMin = min;
							if (max == min)
								addRecipeSafe(registry, () -> new EmiAnvilEnchantRecipe(i, e, finalMin,
										new ResourceLocation("minecraft", "anvil/enchant/" + SyntheticIdentifier.describe(i) + "/" + e.effectId + "/" + SyntheticIdentifier.describe(finalMin))));
							min++;
						}
					}
				}
				VanillaPlugin.addRecipeSafe(registry, () -> new EmiAnvilDisenchantRecipe(i, new ResourceLocation("minecraft", "anvil/disenchant/" + SyntheticIdentifier.describe(i))));
			}
		}

		EmiAgnos.addBrewingRecipes(registry);

		for (int i = 0; i < Item.itemsList.length; ++i) {
			Item item = Item.getItem(i);
			if (item instanceof ItemHoe itemHoe)
				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(Block.dirt), EmiStack.of(itemHoe), EmiStack.of(Block.tilledField), new ResourceLocation("minecraft", item + "/tilling")));
			if (item instanceof ItemMattock itemMattock)
				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(Block.dirt), EmiStack.of(itemMattock), EmiStack.of(Block.tilledField), new ResourceLocation("MITE", item + "/tilling")));
			if (item instanceof ItemMeat itemMeat && !itemMeat.is_cooked && itemMeat != Item.rottenFlesh)
				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(itemMeat), EmiStack.of(Block.fire), EmiStack.of(itemMeat.getCookedItem()), new ResourceLocation("MITE", item + String.valueOf(item.itemID) + "/fire")));
		}

		for (Item item : EmiArmorDyeRecipe.DYEABLE_ITEMS) {
			if (!hiddenItems.contains(item)) {
				continue;
			}
			EmiStack cauldron = EmiStack.of(Item.cauldron);
			EmiStack waterThird = EmiStack.of(Block.waterMoving
//					, FluidUnit.BOTTLE
			);
			int uniq = EmiUtil.RANDOM.nextInt();
			addRecipeSafe(registry, () -> EmiWorldInteractionRecipe.builder()
					.id(synthetic("world/cauldron_washing", EmiUtil.subId(item)))
					.leftInput(EmiStack.EMPTY, s -> new GeneratedSlotWidget(r -> {
						ItemStack stack = new ItemStack(item);
						if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display")) {
							stack.getTagCompound().getCompoundTag("display").removeTag("Color");
						}
						return EmiStack.of(stack);
					}, uniq, s.getBounds().x(), s.getBounds().y()))
					.rightInput(cauldron, true)
					.rightInput(waterThird, false)
					.output(EmiStack.of(item))
					.supportsRecipeTree(false)
					.build());
		}

		EmiStack water = EmiStack.of(Block.waterStill
//				, FluidUnit.BUCKET
		);
		EmiStack waterBottle = EmiStack.of(Block.waterStill
//				, FluidUnit.BOTTLE
		);
		EmiStack lava = EmiStack.of(Block.lavaStill
//				, FluidUnit.BUCKET
		);
		EmiStack waterCatalyst = water.copy().setRemainder(water);
		EmiStack lavaCatalyst = lava.copy().setRemainder(lava);

		addRecipeSafe(registry, () -> EmiWorldInteractionRecipe.builder()
				.id(synthetic("world/fluid_spring", "minecraft/water"))
				.leftInput(waterCatalyst)
				.rightInput(waterCatalyst, false)
				.output(EmiStack.of(Block.waterStill
//						, FluidUnit.BUCKET
				))
				.build());
		addRecipeSafe(registry, () -> EmiWorldInteractionRecipe.builder()
				.id(synthetic("world/fluid_interaction", "minecraft/cobblestone"))
				.leftInput(waterCatalyst)
				.rightInput(lavaCatalyst, false)
				.output(EmiStack.of(Block.cobblestone))
				.build());
		addRecipeSafe(registry, () -> EmiWorldInteractionRecipe.builder()
				.id(synthetic("world/fluid_interaction", "minecraft/stone"))
				.leftInput(waterCatalyst)
				.rightInput(lavaCatalyst, false)
				.output(EmiStack.of(Block.stone))
				.build());
		addRecipeSafe(registry, () -> EmiWorldInteractionRecipe.builder()
				.id(synthetic("world/fluid_interaction", "minecraft/obsidian"))
				.leftInput(lava)
				.rightInput(waterCatalyst, false)
				.output(EmiStack.of(Block.obsidian))
				.build());

		//need minecraft forge
//		for (var entry : LiquidContainerRegistry.getRegisteredLiquidContainerData()) {
//			Fluid fluid = Fluid.of(entry.stillLiquid);
//			if (entry.container.itemID == Items.BUCKET.itemID) {
//				ItemStack bucket = entry.filled;
//				addRecipeSafe(registry, () -> basicWorld(EmiStack.of(Items.BUCKET), EmiStack.of(fluid, FluidUnit.BUCKET), EmiStack.of(bucket),
//						synthetic("emi", "bucket_filling/" + EmiUtil.subId(fluid)), false));
//			}
//		}

		addRecipeSafe(registry, () -> basicWorld(EmiStack.of(Item.glassBottle), waterBottle,
				EmiStack.of(new ItemStack(Item.potion)),
				synthetic("world/unique", "minecraft/water_bottle")));


		for (TagKey<?> key : EmiTags.TAGS) {
			if (new TagEmiIngredient(key, 1).getEmiStacks().size() > 1) {
				addRecipeSafe(registry, () -> new EmiTagRecipe(key));
			}
		}
		
		addFuel(registry, hiddenItems);
	}
	
	private static void addFuel(EmiRegistry registry, PredicateAsSet<Item> hiddenItems) {
		Map<Prototype, Integer> fuelMap = EmiAgnos.getFuelMap();
		Map<Prototype, Integer> heatMap = EmiAgnos.getHeatMap();
		compressRecipesToTags(fuelMap.keySet(), Comparator.comparingInt(fuelMap::get), tag -> {
			EmiIngredient stack = EmiIngredient.of(tag);
			Prototype item = Prototype.of(stack.getEmiStacks().get(0).getItemStack());
			int time = fuelMap.getOrDefault(item, 0);
			int heat = heatMap.getOrDefault(item, 0);
			registry.addRecipe(new EmiFuelRecipe(stack, time, heat, synthetic("fuel/tag", EmiUtil.subId(tag.id()))));
		}, item -> {
			if (!hiddenItems.contains(item.getItem())) {
				int time = fuelMap.get(item);
				int heat = heatMap.get(item);
				registry.addRecipe(new EmiFuelRecipe(EmiStack.of(item), time, heat,
						synthetic("fuel/item", EmiUtil.subId(item.getItem()) + "/" + item.toStack().getItemSubtype())));
			}
		});
	}
	
	private static void compressRecipesToTags(Set<Prototype> stacks, Comparator<Prototype> comparator, Consumer<TagKey<Prototype>> tagConsumer,
			Consumer<Prototype> itemConsumer) {
		Set<Prototype> handled = Sets.newHashSet();
		outer:
		for (TagKey<Prototype> key : EmiTags.getTags(Prototype.class)) {
			List<Prototype> items = key.get();
			if (items.size() < 2) {
				continue;
			}
			Prototype base = items.get(0);
			if (!stacks.contains(base)) {
				continue;
			}
			for (int i = 1; i < items.size(); i++) {
				Prototype item = items.get(i);
				if (!stacks.contains(item) || comparator.compare(base, item) != 0) {
					continue outer;
				}
			}
			if (handled.containsAll(items)) {
				continue;
			}
			handled.addAll(items);
			tagConsumer.accept(key);
		}
		for (Prototype item : stacks) {
			if (handled.contains(item)) {
				continue;
			}
			itemConsumer.accept(item);
		}
	}
	
	private static ResourceLocation synthetic(String type, String name) {
		return new ResourceLocation("emi", "/" + type + "/" + name);
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
			EmiReloadLog.warn("Exception when parsing vanilla recipe " + recipe, e);
		}
	}
	
	private static EmiRenderable simplifiedRenderer(int u, int v) {
		return (raw, x, y, delta) -> {
			EmiDrawContext context = EmiDrawContext.wrap(raw);
			context.drawTexture(EmiRenderHelper.WIDGETS, x, y, u, v, 16, 16);
		};
	}

	private static void addConcreteRecipe(EmiRegistry registry, Block powder, EmiStack water, Block result) {
		addRecipeSafe(registry, () -> basicWorld(EmiStack.of(powder), water, EmiStack.of(result),
			synthetic("world/concrete", EmiUtil.subId(result))));
	}

	private static EmiRecipe basicWorld(EmiIngredient left, EmiIngredient right, EmiStack output, ResourceLocation id) {
		return basicWorld(left, right, output, id, true);
	}

	private static EmiRecipe basicWorld(EmiIngredient left, EmiIngredient right, EmiStack output, ResourceLocation id, boolean catalyst) {
		return EmiWorldInteractionRecipe.builder()
			.id(id)
			.leftInput(left)
			.rightInput(right, catalyst)
			.output(output)
			.build();
	}
}