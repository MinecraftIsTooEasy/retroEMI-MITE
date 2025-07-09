package moddedmite.emi.platform.fish;

import dev.emi.emi.InputPair;
import dev.emi.emi.Prototype;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.runtime.EmiLog;
import moddedmite.emi.MITEPlugin;
import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import dev.emi.emi.registry.EmiPluginContainer;
import moddedmite.emi.util.TileEntityFurnaceHelper;
import org.apache.commons.lang3.text.WordUtils;
import shims.java.com.unascribed.retroemi.EmiMultiPlugin;
import shims.java.com.unascribed.retroemi.NamedEmiPlugin;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.text.Text;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmiAgnosFish extends EmiAgnos {
	static {
		EmiAgnos.delegate = new EmiAgnosFish();
	}

	@Override
	protected boolean isForgeAgnos() {
		return false;
	}


	public static void poke() {
	}
	
//	@Override
//	protected String getModNameAgnos(String namespace) {
//		Optional<ModContainer> container = FishModLoader.getModContainer(namespace);
//		if (container.isPresent()) {
//			return container.get().getMetadata().getName();
//		}
//		return namespace;
//	}

	@Override
	protected String getModNameAgnos(String namespace) {
		if (namespace.equals("c")) {
			return "Common";
		}
		Optional<ModContainer> container = FishModLoader.getModContainer(namespace);
		if (container.isPresent()) {
			return container.get().getMetadata().getName();
		}
		container = FishModLoader.getModContainer(namespace.replace('_', '-'));
		if (container.isPresent()) {
			return container.get().getMetadata().getName();
		}
		return WordUtils.capitalizeFully(namespace.replace('_', ' '));
	}


	@Override
	protected Path getConfigDirectoryAgnos() {
		return FishModLoader.CONFIG_DIR.toPath();
	}

	@Override
	protected boolean isDevelopmentEnvironmentAgnos() {
		return FishModLoader.isDevelopmentEnvironment();
	}

	@Override
	protected boolean isModLoadedAgnos(String id) {
		return FishModLoader.hasMod(id);
	}

	@Override
	protected List<String> getAllModNamesAgnos(String id) {
		return FishModLoader.getModContainer(id).stream().map(c -> c.getMetadata().getName()).toList();
	}

	@Override
	protected List<String> getAllModAuthorsAgnos(String id) {
		return FishModLoader.getModContainer(id).stream().flatMap(c -> c.getMetadata().getAuthors().stream()).map(p -> p.getName()).distinct().toList();
	}

	@Override
	protected List<EmiPluginContainer> getPluginsAgnos() {
		List<EmiPluginContainer> plugins = new ArrayList<>(FishModLoader.getEntrypointContainers("emi", EmiPlugin.class).stream()
				.map(p -> new EmiPluginContainer(p.getEntrypoint(), p.getProvider().getMetadata().getId())).toList());

		boolean foundMITEPlugins = false;
		for (EmiPluginContainer plugin : plugins) {
			if (plugin.id().equals("emi")) {
				foundMITEPlugins = true;
				break;
			}
		}

		if (!foundMITEPlugins) {
			plugins.add(new EmiPluginContainer(new MITEPlugin(), "mite"));
			plugins.add(new EmiPluginContainer(new VanillaPlugin(), "minecraft"));
		}

		return plugins;
	}

	private Stream<EmiPluginContainer> createPlugin(String clazzName, String id) {
		try {
			var clazz = Class.forName(clazzName);
			if (!EmiPlugin.class.isAssignableFrom(clazz) && !EmiMultiPlugin.class.isAssignableFrom(clazz)) {
				EmiLog.warn("Registered emi entrypoint for nilmod {} does not implement EmiPlugin");
				return null;
			}
			if (!Runnable.class.isAssignableFrom(clazz)) {
				EmiLog.warn("Registered emi entrypoint for nilmod {} does not implement Runnable (this is required for NilLoader entrypoint compliance)");
				return null;
			}
			var inst = clazz.getConstructor().newInstance();
			Stream<EmiPlugin> stream = inst instanceof EmiPlugin ep ? Stream.of(ep) : Stream.empty();
			if (inst instanceof EmiMultiPlugin emp) stream = Stream.concat(stream, emp.getChildPlugins());
			return stream.map(ep -> new EmiPluginContainer(ep, ep instanceof NamedEmiPlugin n ? id + "/" + n.getName() : id));
		} catch (Throwable t) {
			EmiLog.warn("Unexpected error while attempting to create plugin for nilmod {}");
			return null;
		}
	}

	@Override
	protected void addBrewingRecipesAgnos(EmiRegistry registry) {
		TileEntityBrewingStand tebs = new TileEntityBrewingStand();
		List<Item> ingredience = Arrays.stream(Item.itemsList).filter(i -> i != null && i.isPotionIngredient()).toList();
		
		Map<InputPair, Prototype> recipes = new HashMap<>();
		IntList potions = new IntArrayList();
		potions.add(0); // water bottle
		IntSet seenPotions = new IntLinkedOpenHashSet();
		
		while (!potions.isEmpty()) {
			int[] iter = potions.toIntArray();
			potions.clear();
			for (int potion : iter) {
				seenPotions.add(potion);
				for (Item ing : ingredience) {
					int result = tebs.getPotionResult(potion, new ItemStack(ing));
					List<PotionEffect> inputEffects = Item.potion.getEffects(potion);
					List<PotionEffect> resultEffects = Item.potion.getEffects(result);
					if (((potion <= 0 || inputEffects != resultEffects) &&
							(inputEffects == null || !inputEffects.equals(resultEffects) && resultEffects != null)) ||
							(!ItemPotion.isSplash(potion) && ItemPotion.isSplash(result))) {
						if (potion != result && !seenPotions.contains(result)) {
							potions.add(result);
							recipes.put(new InputPair(new Prototype(ing), new Prototype(Item.potion, potion)), new Prototype(Item.potion, result));
						}
					}
				}
			}
		}
		
		for (Map.Entry<InputPair, Prototype> en : recipes.entrySet()) {
			InputPair i = en.getKey();
			registry.addRecipe(new EmiBrewingRecipe(EmiStack.of(i.potion()), EmiStack.of(i.ingredient()), EmiStack.of(en.getValue()),
					new ResourceLocation("brewing", "/" + SyntheticIdentifier.describe(i.potion()) + "/" + SyntheticIdentifier.describe(i.ingredient()) + "/" +
							SyntheticIdentifier.describe(en.getValue()))));
		}
		// Vanilla potion entries have different meta from brewable potions (!)
		// Remove all those uncraftable potions from the index
		
		registry.removeEmiStacks(
				es -> es.getItemStack() != null && es.getItemStack().getItem() == Item.potion && !seenPotions.contains(es.getItemStack().getItemSubtype()));
		
		// We just did an exhaustive search and determined every legitimately obtainable potion
		// So let's just cram those into the index where they're supposed to go.
		// Sort them into something resembling a logical order, though!
		List<EmiStack> sorted = recipes.values().stream().filter(p -> p.meta() != 0).distinct().sorted((b, a) -> {
			int i = Boolean.compare(ItemPotion.isSplash(a.meta()), ItemPotion.isSplash(b.meta()));
			if (i != 0) {
				return i;
			}
			List<PotionEffect> effA = ((List<PotionEffect>) ((ItemPotion) a.getItem()).getEffects(a.toStack()));
			List<PotionEffect> effB = ((List<PotionEffect>) ((ItemPotion) b.getItem()).getEffects(b.toStack()));
			return listCompare(effA, effB, Comparator.comparingInt(PotionEffect::getPotionID).thenComparingInt(PotionEffect::getAmplifier).thenComparingInt(PotionEffect::getDuration));
		}).map(EmiStack::of).collect(Collectors.toList());
		EmiStack prev = EmiStack.of(new Prototype(Item.potion, 0));
		for (EmiStack potion : sorted) {
			registry.addEmiStackAfter(potion, prev);
		}
	}
	
	private static <T> int listCompare(List<T> a, List<T> b, Comparator<? super T> cmp) {
		Objects.requireNonNull(cmp);
		if (a == b) {
			return 0;
		}
		if (a == null || b == null) {
			return a == null ? -1 : 1;
		}
		
		int length = Math.min(a.size(), b.size());
		for (int i = 0; i < length; i++) {
			T oa = a.get(i);
			T ob = b.get(i);
			if (oa != ob) {
				// Null-value comparison is deferred to the comparator
				int v = cmp.compare(oa, ob);
				if (v != 0) {
					return v;
				}
			}
		}
		
		return a.size() - b.size();
	}
	
//	@SuppressWarnings("RedundantCast")
//	@Override
//	protected List<TooltipComponent> getItemTooltipAgnos(ItemStack stack) {
//		if (MinecraftServerEMI.getIsServer()) {
//			String var5 = stack.getDisplayName();
//
//			if (stack.hasDisplayName())
//			{
//				var5 = EnumChatFormatting.ITALIC + var5 + EnumChatFormatting.RESET;
//			}
//			return Collections.singletonList(TooltipComponent.of(Text.literal(var5)));
//		}
//		else {
//			// I SWEAR TO GOD DON'T YOU FUCKING TOUCH THIS CAST
//			EntityPlayer player = (EntityPlayer) (Object) Minecraft.getMinecraft().thePlayer;
//			while (player == null) {
//				// THE CLASSLOADER IS A LIE
//				player = (EntityPlayer) (Object) Minecraft.getMinecraft().thePlayer;
//				try {
//					Thread.sleep(5);
//				}
//				catch (InterruptedException e) {
//					throw new RuntimeException(e);
//				}
//			}
//			List<String> tip = stack.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips, (Slot) null);
//			for (int i = 0; i < tip.size(); i++) {
//				tip.set(i, "§" + (i == 0 ? Integer.toHexString(stack.getRarity().rarityColor) : "7") + tip.get(i));
//			}
//			return tip.stream().map(Text::literal).map(TooltipComponent::of).collect(Collectors.toList());
//		}
//	}

	@Override
	protected List<TooltipComponent> getItemTooltipAgnos(ItemStack stack) {
		List<String> tip = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips, (Slot) null);
		for (int i = 0; i < tip.size(); i++) {
			tip.set(i, "§" + (i == 0 ? Integer.toHexString(stack.getRarity().rarityColor) : "7") + tip.get(i));
		}
		return tip.stream()
				.map(Text::literal).map(TooltipComponent::of)
				.toList();
	}
	
//	@Override
//	protected Text getFluidNameAgnos(Fluid fluid, NBTTagCompound nbt) {
//		String key;
//		Prototype proto = fluid.getPrototype();
//		if (proto.item().itemID < Block.blocksList.length) {
//			Block block = Block.blocksList[proto.item().itemID];
//			key = block.getLocalizedName();
//		}
//		else {
//			key = proto.toStack().getDisplayName();
//		}
//		if (!StringTranslate.getInstance().containsTranslateKey(key)) {
//			key += ".name";
//		}
//		return Text.translatable(key)
//				.append(Text.literal(Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? String.format(" (#%04d)", fluid.getId()) : ""));
//	}
//
//	@Override
//	protected List<Text> getFluidTooltipAgnos(Fluid fluid, NBTTagCompound nbt) {
//		return Collections.singletonList(getFluidNameAgnos(fluid, nbt));
//	}
	
//	@Override
//	protected void renderFluidAgnos(FluidEmiStack stack, MatrixStack matrices, int x, int y, float delta, int xOff, int yOff, int width, int height) {
//		Prototype proto = stack.getKeyOfType(Fluid.class).getPrototype();
//		if (proto.item().itemID < Block.blocksList.length) {
//			Block block = Block.blocksList[proto.item().itemID];
//			EmiRenderHelper.drawTintedSprite(matrices, block.getUnlocalizedName(), 1, -1, x, y, xOff, yOff, width, height);
//		}
//		else {
//			Item item = proto.item();
//			EmiRenderHelper.drawTintedSprite(matrices, item.getUnlocalizedName(), item.getIconIndex(proto.toStack()).getIconHeight(), -1, x, y, xOff, yOff,
//					width, height);
//		}
//	}
	
	@Override
	protected boolean canBatchAgnos(ItemStack stack) {
		return false;
	}
	
	@Override
	protected Map<Prototype, Integer> getFuelMapAgnos() {
		// fuels are FULLY DYNAMIC in this version. GUESS I'LL ~~DIE~~ *ITERATE THE REGISTRY*
		ArrayList<ItemStack> pain = new ArrayList<ItemStack>();
		for (Item it : Item.itemsList) {
			if (it != null) {
				it.getSubItems(it.itemID, it.getCreativeTab(), pain);
			}
		}
		return pain.stream().filter(TileEntityFurnaceHelper::isItemFuelS).map(Prototype::of).distinct().collect(Collectors.toMap(p -> p,
				p -> (p.toStack().getItem()).getBurnTime(p.toStack())));
	}

	@Override
	protected Map<Prototype, Integer> getHeatMapAgnos() {
		// fuels are FULLY DYNAMIC in this version. GUESS I'LL ~~DIE~~ *ITERATE THE REGISTRY*
		ArrayList<ItemStack> pain = new ArrayList<ItemStack>();
		for (Item it : Item.itemsList) {
			if (it != null) {
				it.getSubItems(it.itemID, it.getCreativeTab(), pain);
			}
		}
		return pain.stream().filter(TileEntityFurnaceHelper::isItemFuelS).map(Prototype::of).distinct().collect(Collectors.toMap(p -> p,
				p -> (p.toStack().getItem()).getHeatLevel(p.toStack())));
	}
}
