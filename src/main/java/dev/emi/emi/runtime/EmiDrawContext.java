package dev.emi.emi.runtime;

import dev.emi.emi.api.stack.EmiIngredient;
import shims.java.com.mojang.blaze3d.systems.RenderSystem;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.util.math.MatrixStack;
import shims.java.net.minecraft.text.OrderedText;
import shims.java.net.minecraft.text.Text;
import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

public class EmiDrawContext {
	private final Minecraft client = Minecraft.getMinecraft();
	private static final EmiDrawContext INSTANCE = new EmiDrawContext();
	public final Gui context = new Gui();
	
	private EmiDrawContext() {}
	
	public static EmiDrawContext instance() {
		return INSTANCE;
	}
	
	public static EmiDrawContext wrap(DrawContext ctx) {
		return INSTANCE;
	}

	public DrawContext raw() {
		return DrawContext.INSTANCE;
	}

	public MatrixStack matrices() {
		return MatrixStack.INSTANCE;
	}
	
	public void push() {
		glPushMatrix();
	}

	public void pop() {
		glPopMatrix();
	}

	public void drawTexture(ResourceLocation texture, int x, int y, int u, int v, int w, int h) {
		GL11.glDisable(GL11.GL_LIGHTING);
		drawTexture(texture, x, y, w, h, u, v, w, h, 256, 256);
	}

	public void drawTexture(ResourceLocation texture, int x, int y, int z, float u, float v, int w, int h) {
		GL11.glDisable(GL11.GL_LIGHTING);
		drawTexture(texture, x, y, z, u, v, w, h, 256, 256);
	}

	public void drawTexture(ResourceLocation texture, int x, int y, int z, float u, float v, int w, int h, int textureWidth, int textureHeight) {
		GL11.glDisable(GL11.GL_LIGHTING);
		drawTexture(texture, x, y, z, w, h, u, v, w, h, textureWidth, textureHeight);
	}

	public void drawTexture(ResourceLocation texture, int x, int y, int w, int h, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
		GL11.glDisable(GL11.GL_LIGHTING);
		drawTexture(texture, x, y, 0, w, h, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}

	public void drawTexture(ResourceLocation texture, int x, int y, int z,
			int w, int h,
			float u, float v,
			int rW, int rH,
			int textureWidth, int textureHeight) {
		client.getTextureManager().bindTexture(texture);
		float uM = 1 / (float) textureWidth;
		float vM = 1 / (float) textureHeight;
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(x + 0, y + h, z, (u +  0) * uM, (v + rH) * vM);
		tess.addVertexWithUV(x + w, y + h, z, (u + rW) * uM, (v + rH) * vM);
		tess.addVertexWithUV(x + w, y + 0, z, (u + rW) * uM, (v +  0) * vM);
		tess.addVertexWithUV(x + 0, y + 0, z, (u +  0) * uM, (v +  0) * vM);
		tess.draw();
	}

	public void fill(int x, int y, int width, int height, int color) {
		this.fillInner(x, y, x + width, y + height, color);
	}

	public void fillInner(int x, int y, int w, int h, int color) {
		int temp;
		if (x < w) {
			temp = x;
			x = w;
			w = temp;
		}
		if (y < h) {
			temp = y;
			y = h;
			h = temp;
		}

		float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
		float red = (float) (color >> 16 & 0xFF) / 255.0f;
		float green = (float) (color >> 8 & 0xFF) / 255.0f;
		float blue = (float) (color & 0xFF) / 255.0f;

		RenderSystem.enableBlend();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		RenderSystem.defaultBlendFunc();
		setColor(red, green, blue, alpha);

		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertex(x, h, 0.0);
		tess.addVertex(w, h, 0.0);
		tess.addVertex(w, y, 0.0);
		tess.addVertex(x, y, 0.0);
		tess.draw();

		RenderSystem.disableBlend();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void drawText(Text text, int x, int y) {
		drawText(text, x, y, -1);
	}

	public void drawText(Text text, int x, int y, int color) {
		client.fontRenderer.drawString(text.asString(), x, y, color);
	}

	public void drawText(OrderedText text, int x, int y, int color) {
		client.fontRenderer.drawString(text.asString(), x, y, color);
	}

	public void drawTextWithShadow(Text text, int x, int y) {
		drawTextWithShadow(text, x, y, -1);
	}

	public void drawTextWithShadow(Text text, int x, int y, int color) {
		client.fontRenderer.drawStringWithShadow(text.asString(), x, y, color);
	}

	public void drawTextWithShadow(OrderedText text, int x, int y, int color) {
		client.fontRenderer.drawStringWithShadow(text.asString(), x, y, color);
	}

	public void drawCenteredText(Text text, int x, int y) {
		drawCenteredText(text, x, y, -1);
	}

	public void drawCenteredText(Text text, int x, int y, int color) {
		client.fontRenderer.drawString(text.asString(), x - client.fontRenderer.getStringWidth(text.asString()) / 2, y, color);
	}

	public void drawCenteredTextWithShadow(Text text, int x, int y) {
		drawCenteredTextWithShadow(text, x, y, -1);
	}

	public void drawCenteredTextWithShadow(Text text, int x, int y, int color) {
		client.fontRenderer.drawStringWithShadow(text.asString(), x - client.fontRenderer.getStringWidth(text.asString()) / 2, y, color);
	}

	public void resetColor() {
		setColor(1f, 1f, 1f, 1f);
	}

	public void setColor(float r, float g, float b) {
		setColor(r, g, b, 1f);
	}

	public void setColor(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}

	public void drawStack(EmiIngredient stack, int x, int y) {
		stack.render(raw(), x, y, client.timer.renderPartialTicks);
	}

	public void drawStack(EmiIngredient stack, int x, int y, int flags) {
		drawStack(stack, x, y, client.timer.renderPartialTicks, flags);
	}

	public void drawStack(EmiIngredient stack, int x, int y, float delta, int flags) {
		stack.render(raw(), x, y, delta, flags);
	}
}
