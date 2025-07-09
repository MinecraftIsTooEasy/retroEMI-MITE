package shims.java.net.minecraft.util;

public enum Formatting {
	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	OBFUSCATED('k'),
	BOLD('l'),
	STRIKETHROUGH('m'),
	UNDERLINE('n'),
	ITALIC('o'),
	RESET('r');

	private final String code;

	Formatting(char c) {
		this.code = "§" + c;
	}
	
	@Override
	public String toString() {
		return code;
	}

	public Integer getColorValue() {
		return switch (this) {
			case BLACK -> 0x000000;
			case DARK_BLUE -> 0x0000AA;
			case DARK_GREEN -> 0x00AA00;
			case DARK_AQUA -> 0x00AAAA;
			case DARK_RED -> 0xAA0000;
			case DARK_PURPLE -> 0xAA00AA;
			case GOLD -> 0xFFAA00;
			case GRAY -> 0xAAAAAA;
			case DARK_GRAY -> 0x555555;
			case BLUE -> 0x5555FF;
			case GREEN -> 0x55FF55;
			case AQUA -> 0x55FFFF;
			case RED -> 0xFF5555;
			case LIGHT_PURPLE -> 0xFF55FF;
			case YELLOW -> 0x55FFFF;
			case WHITE -> 0xFFFFFF;
			default -> null;
		};
	}

}
