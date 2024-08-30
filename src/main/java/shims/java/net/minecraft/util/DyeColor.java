package shims.java.net.minecraft.util;

import net.minecraft.EntitySheep;
import net.minecraft.ItemDye;

import static net.minecraft.BlockColored.getBlockFromDye;

public enum DyeColor {
	WHITE,
	ORANGE,
	MAGENTA,
	LIGHT_BLUE,
	YELLOW,
	LIME,
	PINK,
	GRAY,
	LIGHT_GRAY,
	CYAN,
	PURPLE,
	BLUE,
	BROWN,
	GREEN,
	RED,
	BLACK,
	;

	public float[] getColorComponents() {
		return EntitySheep.fleeceColorTable[getBlockFromDye(ordinal())];
	}

	public int getFireworkColor() {
		return ItemDye.dyeColors[ordinal()];
	}
}
