package emi.dev.emi.emi.screen.widget.config;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.screen.ConfigScreen;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Supplier;

public class BooleanWidget extends ConfigEntryWidget {
	private final ConfigScreen.Mutator<Boolean> mutator;
	private ButtonWidget button;
	
	public BooleanWidget(Text name, List<TooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<Boolean> mutator) {
		super(name, tooltip, search, 20);
		this.mutator = mutator;
		
		button = EmiPort.newButton(0, 0, 150, 20, getText(), button -> {
			mutator.set(!mutator.get());
			button.setMessage(getText());
		});
		this.setChildren(List.of(button));
	}
	
	public Text getText() {
		if (mutator.get()) {
			return EmiPort.literal("true", Formatting.GREEN);
		}
		else {
			return EmiPort.literal("false", Formatting.RED);
		}
	}
	
	@Override
	public void update(int y, int x, int width, int height) {
		button.x = x + width - button.getWidth();
		button.y = y;
	}
}
