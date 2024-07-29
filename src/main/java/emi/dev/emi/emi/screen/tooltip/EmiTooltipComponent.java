package emi.dev.emi.emi.screen.tooltip;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.FontRenderer;
import net.minecraft.Minecraft;
import net.minecraft.RenderItem;

public interface EmiTooltipComponent extends TooltipComponent {
	
	default void drawTooltip(EmiDrawContext context, TooltipRenderData tooltip) {
	}
	
	default void drawTooltipText(TextRenderData text) {
	}
	
	@Override
	default void drawItems(FontRenderer fontRenderer, int x, int y) {
		EmiDrawContext context = EmiDrawContext.instance();
		context.push();
		context.matrices().translate(x, y, 0);
		Minecraft client = Minecraft.getMinecraft();
		drawTooltip(context, new TooltipRenderData(fontRenderer, RetroEMI.instance.itemRenderer, x, y));
		context.pop();
	}
	
	@Override
	default void drawText(FontRenderer fontRenderer, int x, int y) {
		drawTooltipText(new TextRenderData(fontRenderer, x, y));
	}
	
	public static class TextRenderData {
		public final FontRenderer renderer;
		public final int x, y;
		
		public TextRenderData(FontRenderer renderer, int x, int y) {
			this.renderer = renderer;
			this.x = x;
			this.y = y;
		}
		
		public void draw(String text, int x, int y, int color, boolean shadow) {
			draw(EmiPort.literal(text), x, y, color, shadow);
		}
		
		public void draw(Text text, int x, int y, int color, boolean shadow) {
			renderer.drawString(text.asString(), x + this.x, y + this.y, color, shadow);
		}
	}
	
	public static class TooltipRenderData {
		public final FontRenderer text;
		public final RenderItem item;
		public final int x, y;
		
		public TooltipRenderData(FontRenderer text, RenderItem item, int x, int y) {
			this.text = text;
			this.item = item;
			this.x = x;
			this.y = y;
		}
	}
}
