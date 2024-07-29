package emi.dev.emi.emi.screen.widget.config;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.Element;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.Minecraft;

import java.util.List;

public class GroupNameWidget extends ListWidget.Entry {
	protected static final Minecraft CLIENT = Minecraft.getMinecraft();
	public final String id;
	public final Text text;
	public final List<ConfigEntryWidget> children = Lists.newArrayList();
	public boolean collapsed = false;
	
	public GroupNameWidget(String id, Text text) {
		this.id = id;
		this.text = text;
	}
	
	@Override
	public void render(DrawContext raw, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		context.drawCenteredTextWithShadow(text, x + width / 2, y + 3, -1);
		if (hovered || collapsed) {
			String collapse = "[-]";
			int cx = x + width / 2 - CLIENT.fontRenderer.getStringWidth(text.asString()) / 2 - 20;
			if (collapsed) {
				collapse = "[+]";
			}
			context.drawTextWithShadow(EmiPort.literal(collapse), cx, y + 3, -1);
		}
	}
	
	@Override
	public int getHeight() {
		for (ConfigEntryWidget w : children) {
			if (w.isVisible()) {
				return 20;
			}
		}
		return 0;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			collapsed = !collapsed;
			Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1, 1);
			return true;
		}
		return false;
	}
	
	@Override
	public List<? extends Element> children() {
		return List.of();
	}
}
