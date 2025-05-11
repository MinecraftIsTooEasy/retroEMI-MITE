package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import shims.java.net.minecraft.client.util.math.MatrixStack;
import shims.java.net.minecraft.text.Text;

import java.util.List;

import static org.lwjgl.opengl.GL11.glColor4f;

public class ConfigJumpButton extends SizedButtonWidget {
	
	public ConfigJumpButton(int x, int y, int u, int v, ButtonWidget.PressAction action, List<Text> text) {
		super(x, y, 16, 16, u, v, () -> true, action, text);
		this.texture = EmiRenderHelper.CONFIG;
	}
	
	@Override
	protected int getV(int mouseX, int mouseY) {
		return this.v;
	}

	@Override
	public void renderWidget(DrawContext raw, int mouseX, int mouseY, float delta) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		if (this.isMouseOver(mouseX, mouseY)) {
			context.setColor(0.5F, 0.6F, 1F);
		} else {
			context.setColor(1.0F, 1.0F, 1.0F);
		}
		context.push();
		context.matrices().translate(0, 0, 100);
		super.renderWidget(raw, mouseX, mouseY, delta);
		context.pop();
		context.resetColor();
	}
}
