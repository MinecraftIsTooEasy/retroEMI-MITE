package emi.dev.emi.emi.api.stack;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.Prototype;
import emi.dev.emi.emi.api.render.EmiRender;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.registry.EmiTags;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import emi.dev.emi.emi.screen.tooltip.TagTooltipComponent;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.registry.tag.TagKey;
import emi.shims.java.net.minecraft.registry.tag.WildcardItemTag;
import emi.shims.java.net.minecraft.util.Formatting;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class TagEmiIngredient implements EmiIngredient {
	private final ResourceLocation id;
	private List<EmiStack> stacks;
	public final TagKey<?> key;
	private long amount;
	private float chance = 1;
	
	@ApiStatus.Internal
	public TagEmiIngredient(TagKey<?> key, long amount) {
		this(key, fromKey(key), amount);
	}
	
	private static List<EmiStack> fromKey(TagKey<?> key) {
		if (key instanceof WildcardItemTag) {
			return ((List<Prototype>) key.get()).stream().map(EmiStack::of).collect(Collectors.toList());
		}
		throw new UnsupportedOperationException("Unsupported tag registry " + key);
	}
	
	@ApiStatus.Internal
	public TagEmiIngredient(TagKey<?> key, List<EmiStack> stacks, long amount) {
		this.id = key.id();
		this.key = key;
		this.stacks = stacks;
		this.amount = amount;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TagEmiIngredient tag && tag.key.equals(this.key);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public EmiIngredient copy() {
		EmiIngredient stack = new TagEmiIngredient(key, amount);
		stack.setChance(chance);
		return stack;
	}
	
	@Override
	public List<EmiStack> getEmiStacks() {
		return stacks;
	}
	
	@Override
	public long getAmount() {
		return amount;
	}
	
	@Override
	public EmiIngredient setAmount(long amount) {
		this.amount = amount;
		return this;
	}
	
	@Override
	public float getChance() {
		return chance;
	}
	
	@Override
	public EmiIngredient setChance(float chance) {
		this.chance = chance;
		return this;
	}
	
	@Override
	public void render(DrawContext draw, int x, int y, float delta, int flags) {
		EmiDrawContext context = EmiDrawContext.wrap(draw);
		Minecraft client = Minecraft.getMinecraft();
		
		if ((flags & RENDER_ICON) != 0) {
			if (!EmiTags.hasCustomModel(key)) {
				if (stacks.size() > 0) {
					stacks.get(0).render(context.raw(), x, y, delta, -1 ^ RENDER_AMOUNT);
				}
			}
			else {
				// TODO tag textures, (todo from RetroEMI)
			}
		}
		if ((flags & RENDER_AMOUNT) != 0) {
			String count = "";
			if (amount != 1) {
				count += amount;
			}
			EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal(count));
		}
		if ((flags & RENDER_INGREDIENT) != 0) {
			EmiRender.renderTagIcon(this, context.raw(), x, y);
		}
		if ((flags & RENDER_REMAINDER) != 0) {
			EmiRender.renderRemainderIcon(this, context.raw(), x, y);
		}
	}
	
	@Override
	public List<TooltipComponent> getTooltip() {
		List<TooltipComponent> list = Lists.newArrayList();
		list.add(TooltipComponent.of(EmiPort.ordered(EmiTags.getTagName(key))));
		if (EmiUtil.showAdvancedTooltips()) {
			list.add(TooltipComponent.of(EmiPort.ordered(EmiPort.literal("#" + id, Formatting.DARK_GRAY))));
		}
		if (EmiConfig.appendModId) {
			String mod = EmiUtil.getModName(id.getResourceDomain());
			list.add(TooltipComponent.of(EmiPort.ordered(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC))));
		}
		list.add(new TagTooltipComponent(stacks));
		for (EmiStack stack : stacks) {
			if (!stack.getRemainder().isEmpty()) {
				list.add(new RemainderTooltipComponent(this));
				break;
			}
		}
		return list;
	}
}