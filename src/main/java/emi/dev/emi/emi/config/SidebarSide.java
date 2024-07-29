package emi.dev.emi.emi.config;

import emi.dev.emi.emi.EmiPort;
import emi.shims.java.net.minecraft.text.Text;

public enum SidebarSide implements ConfigEnum {
	NONE("none"), LEFT("left"), RIGHT("right"), TOP("top"), BOTTOM("bottom"),
	;
	
	private final String name;
	
	private SidebarSide(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Text getText() {
		return EmiPort.translatable("emi.sidebar." + name);
	}
}
