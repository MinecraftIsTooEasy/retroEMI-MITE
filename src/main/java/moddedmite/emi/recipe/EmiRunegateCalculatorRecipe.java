package moddedmite.emi.recipe;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.runtime.EmiDrawContext;
import moddedmite.emi.api.recipe.MITEEmiRecipeCategories;
import moddedmite.emi.screen.RunegateCalculatorScreen;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import shims.java.net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class EmiRunegateCalculatorRecipe implements EmiRecipe {
	private final EmiIngredient mithrilSet;
	private final EmiIngredient adamantiumSet;
	private final List<EmiIngredient> inputs;

	public EmiRunegateCalculatorRecipe() {
		List<EmiIngredient> mithrilVariants = new ArrayList<>();
		List<EmiIngredient> adamantiumVariants = new ArrayList<>();
		for (int metadata = 0; metadata < 16; metadata++) {
			mithrilVariants.add(EmiStack.of(new ItemStack(Block.runestoneMithril, 1, metadata)));
			adamantiumVariants.add(EmiStack.of(new ItemStack(Block.runestoneAdamantium, 1, metadata)));
		}
		mithrilSet = EmiIngredient.of(mithrilVariants);
		adamantiumSet = EmiIngredient.of(adamantiumVariants);
		inputs = List.of(EmiStack.of(Block.obsidian), mithrilSet, adamantiumSet);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return MITEEmiRecipeCategories.RUNEGATE;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return new ResourceLocation("mite", "runegate/calculator");
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of();
	}

	@Override
	public int getDisplayWidth() {
		return 160;
	}

	@Override
	public int getDisplayHeight() {
		return 48;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addText(EmiPort.translatable("screen.emi.runegate.page.title"), 4, 4, 0x404040, false);
		widgets.addSlot(EmiStack.of(Block.obsidian), 6, 20);
		widgets.addSlot(mithrilSet, 28, 20);
		widgets.addSlot(adamantiumSet, 50, 20);
		widgets.add(new OpenCalculatorWidget(80, 20, 74, 20));
		widgets.addTooltipText(
				List.of(
						EmiPort.translatable("screen.emi.runegate.page.tooltip"),
						EmiPort.translatable("screen.emi.runegate.page.tooltip_key")),
				80, 20, 74, 20);
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}

	private void openCalculator() {
		Minecraft client = Minecraft.getMinecraft();
		if (client != null) {
			client.displayGuiScreen(new RunegateCalculatorScreen(client.currentScreen));
		}
	}

	private class OpenCalculatorWidget extends Widget {
		private final Bounds bounds;

		private OpenCalculatorWidget(int x, int y, int width, int height) {
			this.bounds = new Bounds(x, y, width, height);
		}

		@Override
		public Bounds getBounds() {
			return bounds;
		}

		@Override
		public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
			EmiDrawContext context = EmiDrawContext.wrap(draw);
			boolean hovered = bounds.contains(mouseX, mouseY);
			int base = hovered ? 0xFF8F8F8F : 0xFF7A7A7A;
			context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), base);
			context.fill(bounds.x(), bounds.y(), bounds.width(), 1, 0xFFBFBFBF);
			context.fill(bounds.x(), bounds.y(), 1, bounds.height(), 0xFFBFBFBF);
			context.fill(bounds.x(), bounds.y() + bounds.height() - 1, bounds.width(), 1, 0xFF4A4A4A);
			context.fill(bounds.x() + bounds.width() - 1, bounds.y(), 1, bounds.height(), 0xFF4A4A4A);
			context.drawTextWithShadow(EmiPort.translatable("screen.emi.runegate.page.open"),
					bounds.x() + 7, bounds.y() + 6, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int button) {
			openCalculator();
			return true;
		}
	}
}
