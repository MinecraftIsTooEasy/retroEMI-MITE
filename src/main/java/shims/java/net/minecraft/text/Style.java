package shims.java.net.minecraft.text;

import net.minecraft.ChatClickData;
import shims.java.net.minecraft.util.Formatting;

public class Style {

	public static final Style EMPTY = new Style("");
	
	private final String formats;
	private final ChatClickData clickEvent;

	public Style(String formats) {
		this.formats = formats;
		this.clickEvent = (ChatClickData) null;
	}

	public Style(ChatClickData clickEvent) {
		this.formats = "";
		this.clickEvent = clickEvent;
	}
	
	public Style withUnderline(boolean underline) {
		if (underline) return new Style(formats + Formatting.UNDERLINE);
		return this;
	}
	
	public Style withColor(int color) {
		return new Style(formats + "§x" + (Integer.toHexString(color|0xFF000000).substring(2).replace("", "§")) + "x");
	}

	public Style withFormatting(Formatting f) {
		return new Style(formats + f);
	}

	public Style withClickEvent(ChatClickData clickEvent) {
		return new Style(clickEvent);
	}
	
	@Override
	public String toString() {
		return formats;
	}
	
}
