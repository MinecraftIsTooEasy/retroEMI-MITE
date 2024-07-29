package emi.shims.java.net.minecraft.client.gui.tooltip;

import emi.shims.java.net.minecraft.client.util.math.Vec2i;
import net.minecraft.GuiScreen;

public interface TooltipPositioner {
	public Vec2i getPosition(GuiScreen screen, int x, int y, int w, int h);
}
