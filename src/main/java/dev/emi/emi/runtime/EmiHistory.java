package dev.emi.emi.runtime;

import com.google.common.collect.Lists;
import dev.emi.emi.api.EmiApi;
import net.minecraft.GuiContainer;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;

import java.util.List;

public class EmiHistory {
	private static final List<GuiScreen> HISTORIES = Lists.newArrayList();
	
	public static boolean isEmpty() {
		return HISTORIES.isEmpty();
	}

	public static void push(GuiScreen history) {
		HISTORIES.add(history);
	}

	public static void pop() {
		Minecraft client = Minecraft.getMinecraft();
		if (client.currentScreen instanceof GuiContainer) {
			clear();
			return;
		}
		int i = HISTORIES.size() - 1;
		GuiContainer screen = EmiApi.getHandledScreen();
		if (screen != null) {
			if (i >= 0) {
				client.displayGuiScreen(HISTORIES.remove(i));
			} else {
				client.displayGuiScreen(screen);
			}
		}
	}

	public static void clear() {
		HISTORIES.clear();
	}
}
