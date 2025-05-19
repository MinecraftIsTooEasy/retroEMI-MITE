package shims.java.net.minecraft.client.gui.widget;

import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.MathHelper;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;
import shims.java.com.mojang.blaze3d.systems.RenderSystem;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.client.util.math.MatrixStack;
import shims.java.net.minecraft.text.MutableText;
import shims.java.net.minecraft.text.Text;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.GuiButton;
import net.minecraft.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ButtonWidget
		extends ClickableWidget {
	public static final int DEFAULT_WIDTH_SMALL = 120;
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	protected static final NarrationSupplier DEFAULT_NARRATION_SUPPLIER = textSupplier -> (MutableText) textSupplier.get();
	protected final PressAction onPress;
	protected final NarrationSupplier narrationSupplier;
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

	public static Builder builder(Text message, PressAction onPress) {
		return new Builder(message, onPress);
	}

	protected ButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
		super(x, y, width, height, message);
		this.onPress = onPress;
		this.narrationSupplier = narrationSupplier;
	}

	public void renderWidget(DrawContext raw, int mouseX, int mouseY, float delta) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		context.pop();
		context.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.drawTexture(buttonTextures, this.getX(), this.getY(), 0, 46 + this.getHoverState() * 20, this.width / 2, this.height);
		context.drawTexture(buttonTextures, this.getX() + this.width / 2, this.getY(), 200 - (this.width - this.width / 2), 46 + this.getHoverState() * 20, this.width - this.width / 2, this.height);
		int i = 0xFFFFFF;
		if (!this.active) i = 0xA0A0A0;
		else if (this.isHovered()) i = 0xFFFFFA0;
		context.drawCenteredTextWithShadow(this.getMessage(),this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, i | MathHelper.ceiling_double_int(this.alpha * 255.0F) << 24);
		context.push();
		context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		onPress();
	}

	public void onPress() {
		this.onPress.onPress(this);
	}

	protected int getHoverState() {
		byte state = 1;
		if (!this.active) {
			state = 0;
		} else if (this.hovered) {
			state = 2;
		}
		return state;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Builder {
		private final Text message;
		private final PressAction onPress;
		@Nullable
		private List<TooltipComponent> tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

		public Builder(Text message, PressAction onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder dimensions(int x, int y, int width, int height) {
			return this.position(x, y).size(width, height);
		}

		public Builder tooltip(@Nullable List<TooltipComponent> tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
			this.narrationSupplier = narrationSupplier;
			return this;
		}

		public ButtonWidget build() {
			ButtonWidget buttonWidget = new ButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
			buttonWidget.setTooltip(this.tooltip);
			return buttonWidget;
		}
	}

	@Environment(value = EnvType.CLIENT)
	public static interface PressAction {
		public void onPress(ButtonWidget var1);
	}

	@Environment(value = EnvType.CLIENT)
	public static interface NarrationSupplier {
		public MutableText createNarrationMessage(Supplier<MutableText> var1);
	}
}
