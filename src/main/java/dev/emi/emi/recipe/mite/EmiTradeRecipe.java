//package dev.emi.emi.recipe.mite;
//
//import emi.dev.emi.emi.api.recipe.MITEEmiRecipeCategories;
//import emi.dev.emi.emi.api.recipe.EmiRecipe;
//import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
//import emi.dev.emi.emi.api.render.EmiTexture;
//import emi.dev.emi.emi.api.stack.EmiIngredient;
//import emi.dev.emi.emi.api.stack.EmiStack;
//import emi.dev.emi.emi.api.widget.SlotWidget;
//import emi.dev.emi.emi.api.widget.TextureWidget;
//import emi.dev.emi.emi.api.widget.WidgetHolder;
//import dev.emi.emi.runtime.EmiDrawContext;
//import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
//import shims.java.net.minecraft.text.Text;
//import shims.java.net.minecraft.util.SyntheticIdentifier;
//import net.minecraft.*;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class EmiTradeRecipe implements EmiRecipe {
//	private final ResourceLocation TEXTURE = new ResourceLocation("emi", "textures/recipe/btwwidgets.png");
//	private Random rand = new Random();
//	private final VillagerTrade trade;
//	private final Class<?> professionClass;
//	public final int tradeLevel;
//	public final boolean isLevelUp;
//	public final int professionId;
//	private final TradeItem firstInput;
//	private final TradeItem secondInput;
//	private final TradeItem output;
//
//	public EmiTradeRecipe(VillagerTrade trade, int profession, boolean isLevelUp) {
//		this.trade = trade;
//		this.professionId = profession;
//		this.isLevelUp = isLevelUp;
//		this.professionClass = EntityVillager.professionMap.get(profession);
//		this.firstInput = getFirstInput(trade);
//		this.secondInput = getSecondInput(trade);
//		this.output = getOutput(trade);
//	}
//
//	@Override
//	public EmiRecipeCategory getCategory() {
//		return MITEEmiRecipeCategories.TRADING;
//	}
//
//	@Override
//	public @Nullable ResourceLocation getId() {
//		return new SyntheticIdentifier(this);
//	}
//
//	@Override
//	public List<EmiIngredient> getInputs() {
//		List<EmiStack> stackList = new ArrayList<>();
//		stackList.add(idConvert(firstInput, 1));
//		stackList.add(idConvert(secondInput, 1));
//
//		return List.of(EmiIngredient.of(stackList));
//	}
//
//	@Override
//	public List<EmiStack> getOutputs() {
//		return idConvert(output, output.maxCount()).getEmiStacks();
//	}
//
//	@Override
//	public int getDisplayWidth() {
//		return 150;
//	}
//
//	@Override
//	public int getDisplayHeight() {
//		return 32;
//	}
//
//	@Override
//	public boolean supportsRecipeTree() {
//		return false;
//	}
//
//	@Override
//	public void addWidgets(WidgetHolder widgets) {
//		widgets.addSlot(idConvert(firstInput, 1), 30, 0);
//		widgets.addSlot(idConvert(secondInput, 1), 50, 0);
//
//		boolean isRange = output.minCount() != 1 && output.maxCount() != 1 && output.minCount() != output.maxCount();
//		EmiStack outputStack = idConvert(output, isRange ? 1 : output.maxCount());
//
//		SlotWidget outputSlot = widgets.addSlot(outputStack, 100, 0).recipeContext(this).catalyst(false);
//		if (trade instanceof EnchantmentVillagerTrade) {
//			EnchantmentHelper.addRandomEnchantment(rand, outputStack.getItemStack(), rand.nextInt(15));
//			outputSlot.appendTooltip(Text.translatable("emi.trade.enchanted"));
//		}
//
//		TextureWidget arrow = widgets.addTexture(EmiTexture.FULL_ARROW, 72, 0);
//		if (isLevelUp) {
//			for (int i = 0; i < 2; i++) {
//				widgets.addTexture(TEXTURE, 75 + (8 * i), 4, 7, 7, 36, 0);
//			}
//			arrow.tooltip(List.of(TooltipComponent.of(Text.translatable("emi.trade.level_up", -tradeLevel, -tradeLevel + 1))));
//		}
//		else {
//			widgets.addTexture(TEXTURE, 79, 4, 7, 7, 36, 0);
//		}
//
//		widgets.addDrawable(0, 0, getDisplayWidth(), getDisplayHeight(), (draw, mouseX, mouseY, delta) -> {
//			EmiDrawContext context = EmiDrawContext.wrap(draw);
//			if (firstInput.minCount() != 1 && firstInput.maxCount() != 1 && firstInput.minCount() != firstInput.maxCount()) {
//				drawRange(context, 30 * 2, firstInput.minCount(), firstInput.maxCount());
//			}
//			if (secondInput.minCount() != 1 && secondInput.maxCount() != 1 && secondInput.minCount() != secondInput.maxCount()) {
//				drawRange(context, 50 * 2, secondInput.minCount(), secondInput.maxCount());
//			}
//			if (output.minCount() != 1 && output.maxCount() != 1 && output.minCount() != output.maxCount()) {
//				drawRange(context, 100 * 2, output.minCount(), output.maxCount());
//			}
//			Text profession = Text.translatable("emi.trade." + professionClass.getSimpleName(), isLevelUp ? -tradeLevel : tradeLevel);
//			context.drawCenteredTextWithShadow(profession, 75, 21);
//		});
//	}
//
//	private TradeItem getFirstInput(VillagerTrade trade) {
//		TradeItem item = TradeItem.EMPTY;
//		if (trade instanceof EnchantmentVillagerTrade enchTrade) {
//			item = enchTrade.item;
//		}
//		else if (trade instanceof StandardVillagerTrade standardTrade) {
//			item = standardTrade.input;
//		}
//		return item;
//	}
//
//	private TradeItem getSecondInput(VillagerTrade trade) {
//		TradeItem item = TradeItem.EMPTY;
//		if (trade instanceof EnchantmentVillagerTrade enchTrade) {
//			item = enchTrade.cost;
//		}
//		else if (trade instanceof StandardVillagerTrade standardTrade) {
//			item = standardTrade.secondaryInput;
//		}
//		return item;
//	}
//
//	private TradeItem getOutput(VillagerTrade trade) {
//		TradeItem item = TradeItem.EMPTY;
//		if (trade instanceof EnchantmentVillagerTrade enchTrade) {
//			item = enchTrade.item; // Modified later to be enchanted
//		}
//		else if (trade instanceof StandardVillagerTrade standardTrade) {
//			item = standardTrade.output;
//		}
//		return item;
//	}
//
//	private void drawRange(EmiDrawContext context, int xOffset, int minCount, int maxCount) {
//		String text = String.format("%d-%d", minCount, maxCount);
//		int x = (int) (xOffset + (27 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) * 0.5F));
//		context.push();
//		context.matrices().translate(0, 0, 200);
//		context.matrices().scale(.5, .5, .5);
//		context.drawTextWithShadow(Text.literal(text), x, 27);
//		context.pop();
//	}
//
//	private EmiStack idConvert(TradeItem item, int count) {
//		return EmiStack.of(new ItemStack(item.id(), count, item.metadata()));
//	}
//}
