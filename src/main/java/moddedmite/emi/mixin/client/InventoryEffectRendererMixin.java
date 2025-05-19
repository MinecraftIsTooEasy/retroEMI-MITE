package moddedmite.emi.mixin.client;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.config.EffectLocation;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(InventoryEffectRenderer.class)
public abstract class InventoryEffectRendererMixin extends GuiContainer {

    @Shadow private boolean field_74222_o;

    @Shadow @Final private static ResourceLocation sugar_icon;
    @Shadow private int initial_tick;
    @Unique private final int EFFECT_WIDTH = 124;
    @Unique private static final ResourceLocation MITE_icons = new ResourceLocation("textures/gui/MITE_icons.png");


    public InventoryEffectRendererMixin(Container par1Container) {
        super(par1Container);
    }

    @Redirect(method = "initGui()V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/InventoryEffectRenderer;guiLeft:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void conNotBeyond(InventoryEffectRenderer inventoryEffectRenderer, int value) {
        guiLeft = Math.max(guiLeft, EFFECT_WIDTH);
    }

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiContainer;initGui()V", shift = At.Shift.AFTER), cancellable = true)
    private void initGui(CallbackInfo ci) {
        if (EmiConfig.effectLocation == EffectLocation.TOP || EmiConfig.effectLocation == EffectLocation.HIDDEN ) {
            this.field_74222_o = false;
            ci.cancel();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiContainer;drawScreen(IIF)V", shift = At.Shift.AFTER), cancellable = true)
    private void drawScreen(int par1, int par2, float par3, CallbackInfo ci) {
        if (EmiConfig.effectLocation == EffectLocation.TOP) {
            drawCenteredEffects(par1, par2);
            ci.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void displayDebuffEffects() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;

        int debuffX = changeEffectSpace(this.guiLeft - 124);
        int debuffY = this.guiTop;
        boolean wide = !EmiConfig.effectLocation.compressed;
        Collection<PotionEffect> activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
        int num_effects = activePotionEffects.size();
        if (this.mc.thePlayer.isMalnourished()) ++num_effects;
        if (this.mc.thePlayer.isInsulinResistant()) ++num_effects;
        if (this.mc.thePlayer.is_cursed) ++num_effects;

        if (num_effects > 0 && EmiConfig.effectLocation != EffectLocation.HIDDEN) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            int spacing = 33;

            if (num_effects > 5 && EmiConfig.effectLocation != EffectLocation.HIDDEN && EmiConfig.effectLocation != EffectLocation.TOP) {
                spacing = 132 / (num_effects - 1);
            }

            PotionEffect hovered = null;

            this.drawMITEEffects(mouseX, mouseY, debuffX, debuffY, spacing, wide);

            for (Iterator<PotionEffect> iterator = activePotionEffects.iterator(); iterator.hasNext(); debuffY += spacing) {
                PotionEffect potionEffect = iterator.next();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(debuffX, debuffY, wide);

                int ew = wide ? 120 : 32;
                if (mouseX >= debuffX && mouseX < debuffX + ew && mouseY >= debuffY && mouseY < debuffY + 32) {
                    hovered = potionEffect;
                }
                drawPotionIcon(debuffX, debuffY, potionEffect);
                drawStatusEffectDescriptions(debuffX, debuffY, potionEffect, wide);
            }
            if (!wide) {
                renderTooltip(mouseX, mouseY, hovered);
            }
        }
    }

    private void drawMITEEffects(int mouseX, int mouseY, int debuffX, int debuffY, int spacing, boolean wide) {
        if (this.mc.thePlayer.isMalnourished()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            this.mc.getTextureManager().bindTexture(MITE_icons);
            this.drawTexturedModalRect(debuffX + 6, debuffY + 7, 18, 198, 18, 18);
            if (wide) {
                String name = I18n.getString("effect.malnourished");
                this.fontRenderer.drawStringWithShadow(name, debuffX + 10 + 18 - 1, debuffY + 6 + 1, 16777215);
                String description = ((int) this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0 ? I18n.getString("effect.malnourished.slowHealing") : I18n.getString("effect.malnourished.plus50PercentHunger");
                this.fontRenderer.drawStringWithShadow(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 8355711);
            }
            debuffY += spacing;
        }

        if (this.mc.thePlayer.isInsulinResistant()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            this.mc.getTextureManager().bindTexture(sugar_icon);
            this.drawTexturedModalRect2(debuffX + 7, debuffY + 8, 16, 16);
            EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
            GL11.glColor4f(insulin_resistance_level.getRedAsFloat(), insulin_resistance_level.getGreenAsFloat(), insulin_resistance_level.getBlueAsFloat(), 1.0F);
            this.mc.getTextureManager().bindTexture(MITE_icons);
            this.drawTexturedModalRect(debuffX + 6, debuffY + 7, 54, 198, 18, 18);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (wide) {
                String name = I18n.getString("effect.insulinResistance");
                this.fontRenderer.drawStringWithShadow(name, debuffX + 10 + 18 - 1, debuffY + 6 + 1, 16777215);
                String description = StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance());
                this.fontRenderer.drawStringWithShadow(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 8355711);
            }
            debuffY += spacing;
        }

        if (this.mc.thePlayer.is_cursed) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            this.mc.getTextureManager().bindTexture(MITE_icons);
            this.drawTexturedModalRect(debuffX + 6, debuffY + 7, 0, 198, 18, 18);
            if (wide) {
                String name = I18n.getString("effect.cursed");
                this.fontRenderer.drawStringWithShadow(name, debuffX + 10 + 18 - 1, debuffY + 6 + 1, 16777215);
                String description = this.mc.thePlayer.curse_effect_known ? EnumChatFormatting.DARK_PURPLE + this.mc.thePlayer.getCurse().getTitle() : Translator.get("curse.unknown");
                this.fontRenderer.drawStringWithShadow(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 8355711);
            }
            debuffY += spacing;
        }
        if (!wide) {
            renderTooltip(mouseX, mouseY);
        }
    }

    //EMI feature, lots of magic numbers :)
    private void drawCenteredEffects(int mouseX, int mouseY) {
        EmiDrawContext context = EmiDrawContext.instance();
        context.resetColor();
        Minecraft client = Minecraft.getMinecraft();
        Collection<PotionEffect> effects = client.thePlayer.getActivePotionEffects();
        int size = effects.size();
        if (size == 0) {
            return;
        }
        boolean wide = size == 1;
        int y = this.guiTop - 34;
        if (ReflectHelper.dyCast(this) instanceof GuiContainerCreative) {
            y -= 28;
        }
        int xOff = 34;
        if (wide) {
            xOff = 122;
        } else if (size > 5) {
            xOff = (this.width - 32) / (size - 1);
        }
        int width = (size - 1) * xOff + (wide ? 120 : 32);
        int x = this.guiLeft + (this.xSize - width) / 2;
        PotionEffect hovered = null;
        int restoreY = this.ySize;
        try {
            this.ySize = y;
            for (PotionEffect inst : effects) {
                int ew = wide ? 120 : 32;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);

                drawPotionIcon(x, y, inst);
                if (mouseX >= x && mouseX < x + ew && mouseY >= y && mouseY < y + 32) {
                    hovered = inst;
                }
                x += xOff;
                drawStatusEffectDescriptions(x - width, y, inst, wide);
            }
        } finally {
            this.ySize = restoreY;
        }
        if (size > 1) {
            renderTooltip(mouseX, mouseY, hovered);
        }
    }

    @Unique
    private void renderTooltip(int mouseX, int mouseY, PotionEffect effect) {
        if (effect != null) {
            String amplifier = getPotionAmplifier(effect);
            TooltipComponent name = TooltipComponent.of(Text.translatable(effect.getEffectName()).append(Text.literal(amplifier)));
            TooltipComponent duration = TooltipComponent.of(Text.literal(Potion.getDurationString(effect)));
            EmiRenderHelper.drawTooltip(this, EmiDrawContext.instance(), List.of(name, duration), mouseX, Math.max(mouseY, 16));
        }
    }

    @Unique
    private void renderTooltip(int mouseX, int mouseY) {
        TooltipComponent name = null;
        TooltipComponent description = null;
        if (this.mc.thePlayer.isMalnourished()) {
            name = TooltipComponent.of(Text.translatable("effect.malnourished"));
            description = TooltipComponent.of(Text.literal(((int) this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0 ? I18n.getString("effect.malnourished.slowHealing") : I18n.getString("effect.malnourished.plus50PercentHunger")));
        }
        if (this.mc.thePlayer.isInsulinResistant()) {
            name = TooltipComponent.of(Text.translatable("effect.insulinResistance"));
            description = TooltipComponent.of(Text.literal(StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance())));
        }
        if (this.mc.thePlayer.is_cursed) {
            name = TooltipComponent.of(Text.translatable("effect.cursed"));
            description = TooltipComponent.of(Text.literal(this.mc.thePlayer.curse_effect_known ? EnumChatFormatting.DARK_PURPLE + this.mc.thePlayer.getCurse().getTitle() : Translator.get("curse.unknown")));
        }
        if (name != null && description != null) {
            EmiRenderHelper.drawTooltip(this, EmiDrawContext.instance(), List.of(name, description), mouseX, Math.max(mouseY, 16));
        }
    }

    @Unique
    private String getPotionAmplifier(PotionEffect effect) {
        return switch (effect.getAmplifier()) {
            case 1 -> " II";
            case 2 -> " III";
            case 3 -> " IV";
            default -> "";
        };
    }

    @Unique
    private void drawStatusEffectBackgrounds(int x, int y, boolean wide) {
        if (wide) {
            this.drawTexturedModalRect(x, y, 0, 166, 140, 32);
        } else {
            //split so it renders the edge properly
            this.drawTexturedModalRect(x, y, 0, 166, 28, 32);
            this.drawTexturedModalRect(x + 24, y, 116, 166, 40, 32);
        }
    }

    @Unique
    private void drawStatusEffectDescriptions(int x, int y, PotionEffect potionEffect, boolean wide) {
        if (wide) {
            String potionName = I18n.getString(potionEffect.getEffectName()) + getPotionAmplifier(potionEffect);

            this.fontRenderer.drawStringWithShadow(potionName, x + 10 + 18, y + 6, 16777215);
            String durationString = Potion.getDurationString(potionEffect);
            this.fontRenderer.drawStringWithShadow(durationString, x + 10 + 18, y + 16, 8355711);
        }
    }

    @Unique
    private void drawPotionIcon(int x, int y, PotionEffect potionEffect) {
        Potion potionType = Potion.potionTypes[potionEffect.getPotionID()];
        if (potionType.hasStatusIcon()) {
            int statusIconIndex = potionType.getStatusIconIndex();
            this.drawTexturedModalRect(x + 6, y + 7, 0 + statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
        }
    }

    @Unique
    private int changeEffectSpace(int original) {
        return switch (EmiConfig.effectLocation) {
            case RIGHT, RIGHT_COMPRESSED, HIDDEN -> this.guiLeft + this.xSize + 2;
            case TOP -> this.guiLeft;
            case LEFT_COMPRESSED -> this.guiLeft - 2 - 32;
            case LEFT -> original;
        };
    }
}
