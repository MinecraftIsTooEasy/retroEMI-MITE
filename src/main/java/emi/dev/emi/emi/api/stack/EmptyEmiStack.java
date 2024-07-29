package emi.dev.emi.emi.api.stack;

import emi.dev.emi.emi.EmiPort;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.ItemStack;
import net.minecraft.NBTTagCompound;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class EmptyEmiStack extends EmiStack {
	private static final ResourceLocation ID = new ResourceLocation("emi", "empty");
	
	@Override
	public EmiStack getRemainder() {
		return EMPTY;
	}
	
	@Override
	public List<EmiStack> getEmiStacks() {
		return List.of(EMPTY);
	}
	
	@Override
	public EmiStack setRemainder(EmiStack stack) {
		throw new UnsupportedOperationException("Cannot mutate an empty stack");
	}
	
	@Override
	public EmiStack copy() {
		return EMPTY;
	}
	
	public EmiStack setAmount(long amount) {
		return this;
	}
	
	public EmiStack setChance(float chance) {
		return this;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public NBTTagCompound getNbt() {
		return null;
	}
	
	@Override
	public Object getKey() {
		return null;
	}
	
	@Override
	public ItemStack getItemStack() {
		return ItemStacks.EMPTY;
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	public boolean isEqual(EmiStack stack) {
		return stack == EMPTY;
	}
	
	@Override
	public void render(DrawContext draw, int x, int y, float delta, int flags) {
	}
	
	@Override
	public List<Text> getTooltipText() {
		return List.of();
	}
	
	@Override
	public List<TooltipComponent> getTooltip() {
		return List.of();
	}
	
	@Override
	public Text getName() {
		return EmiPort.literal("");
	}
	
	static class EmptyEntry {
	}
}