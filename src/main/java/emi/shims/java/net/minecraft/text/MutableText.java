package emi.shims.java.net.minecraft.text;

import emi.shims.java.net.minecraft.util.Formatting;

public interface MutableText extends Text {

	MutableText setStyle(Style s);
	MutableText formatted(Formatting fmt);
	MutableText formatted(Formatting... fmt);
	MutableText append(Text text);
	
}
