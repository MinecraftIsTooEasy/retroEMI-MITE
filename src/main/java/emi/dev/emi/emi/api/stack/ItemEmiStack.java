package emi.dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.Prototype;
import emi.dev.emi.emi.api.render.EmiRender;
import emi.dev.emi.emi.platform.EmiAgnos;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.dev.emi.emi.screen.StackBatcher;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.Formatting;
import emi.shims.java.net.minecraft.util.NumericIdentifier;
import net.minecraft.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

@ApiStatus.Internal
public class ItemEmiStack extends EmiStack implements StackBatcher.Batchable {
	private final ItemStack stack;
	private boolean unbatchable;
	
	public ItemEmiStack(ItemStack stack) {
		this(stack, stack.stackSize);
	}
	
	public ItemEmiStack(ItemStack stack, long amount) {
		stack = stack.copy();
		stack.stackSize = ((int) amount);
		this.stack = stack;
		this.amount = amount;
	}
	
	@Override
	public ItemStack getItemStack() {
		if (stack == null) {
			return null;
		}
		stack.stackSize = ((int) amount);
		
		return stack;
	}
	
	@Override
	public EmiStack copy() {
		EmiStack e = new ItemEmiStack(stack.copy(), amount);
		e.setChance(chance);
		e.setRemainder(getRemainder().copy());
		e.comparison = comparison;
		return e;
	}
	
	@Override
	public boolean isEmpty() {
		return amount == 0 || ItemStacks.isEmpty(stack);
	}
	
	@Override
	public NBTTagCompound getNbt() {
		return stack.getTagCompound();
	}
	
	@Override
	public Object getKey() {
		return Prototype.of(stack);
	}
	
	@Override
	public ResourceLocation getId() {
		return new NumericIdentifier(stack.itemID);
	}
	
	@Override
	public int hashCode() {
		if (stack == null) {
			return 0;
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + stack.itemID;
		result = prime * result + (stack.getItem().getHasSubtypes() ? stack.getItemSubtype() : 0);
		return result;
	}
	
	@Override
	public void render(DrawContext draw, int x, int y, float delta, int flags) {
		EmiDrawContext context = EmiDrawContext.wrap(draw);
		ItemStack stack = getItemStack();
		if ((flags & RENDER_ICON) != 0) {
//			glEnable(GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			if (stack.getItem() instanceof ItemBlock && stack.getItemSubtype() == 32767) stack.setItemSubtype(0);
			draw.drawItem(stack, x, y);
			draw.drawItemInSlot(Minecraft.getMinecraft().fontRenderer, stack, x, y);
//			RenderHelper.disableStandardItemLighting();
		}
		if ((flags & RENDER_AMOUNT) != 0) {
			String count = "";
			if (amount != 1) {
				count += amount;
			}
			EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal(count));
		}
		if ((flags & RENDER_REMAINDER) != 0) {
			EmiRender.renderRemainderIcon(this, context.raw(), x, y);
		}
	}
	
	@Override
	public boolean isSideLit() {
		return RetroEMI.isSideLit(getItemStack());
	}
	
	@Override
	public boolean isUnbatchable() {
		ItemStack stack = getItemStack();
		return unbatchable || stack.isItemEnchanted() || stack.isItemDamaged() || !EmiAgnos.canBatch(stack);
	}
	
	@Override
	public void setUnbatchable() {
		this.unbatchable = true;
	}
	
	@Override
	public void renderForBatch(DrawContext draw, int x, int y, int z, float delta) {
		//		EmiDrawContext context = EmiDrawContext.wrap(draw);
		//		ItemStack stack = getItemStack();
		//		ItemRenderer ir = client.getItemRenderer();
		//		BakedModel model = ir.getModel(stack, null, null, 0);
		//		context.push();
		//		try {
		//			context.matrices().translate(x, y, 100.0f + z + (model.hasDepth() ? 50 : 0));
		//			context.matrices().translate(8.0, 8.0, 0.0);
		//			context.matrices().scale(16.0f, 16.0f, 16.0f);
		//			ir.renderItem(stack, ModelTransformationMode.GUI, false, context.matrices(), vcp, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		//		} finally {
		//			context.pop();
		//		}
	}
	
	@Override
	public List<Text> getTooltipText() {
		return ((List<String>) getItemStack().getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips, (Slot)null)).stream()
				.map(Text::literal).collect(Collectors.toList());
	}
	
	@Override
	public List<TooltipComponent> getTooltip() {
		ItemStack stack = getItemStack();
		List<TooltipComponent> list = Lists.newArrayList();
		if (!isEmpty()) {
			list.addAll(EmiAgnos.getItemTooltip(stack));
			//String namespace = EmiPort.getItemRegistry().getId(stack.getItem()).getNamespace();
			//String mod = EmiUtil.getModName(namespace);
			//list.add(TooltipComponent.of(EmiLang.literal(mod, Formatting.BLUE, Formatting.ITALIC)));
			list.add(TooltipComponent.of(Text.literal(RetroEMI.getMod(stack)).formatted(Formatting.BLUE, Formatting.ITALIC)));
			list.addAll(super.getTooltip());
		}
		return list;
	}
	
	@Override
	public Text getName() {
		if (isEmpty()) {
			return EmiPort.literal("");
		}
		return Text.literal(getItemStack().getDisplayName());
	}
	
	static class ItemEntry {
	}
}