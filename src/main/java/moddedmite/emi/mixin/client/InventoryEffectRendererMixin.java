package moddedmite.emi.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.config.EffectLocation;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import huix.glacier.api.extension.creativetab.GlacierCreativeTabs;
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
import shims.java.net.minecraft.util.Formatting;

import java.util.*;

@Mixin(value = InventoryEffectRenderer.class, priority = 2000)
public abstract class InventoryEffectRendererMixin extends GuiContainer {

    @Shadow private boolean field_74222_o;

    @Shadow @Final private static ResourceLocation sugar_icon;
    @Shadow private int initial_tick;
    @Unique private final int EFFECT_WIDTH = 124;
    @Unique private static final ResourceLocation MITE_icons = new ResourceLocation("textures/gui/MITE_icons.png");
    @Unique private int debuffY;

    @Unique private static boolean hasRIC;

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
        if (EmiConfig.effectLocation == EffectLocation.TOP || EmiConfig.effectLocation == EffectLocation.HIDDEN) {
            this.field_74222_o = false;
            ci.cancel();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiContainer;drawScreen(IIF)V", shift = At.Shift.AFTER), cancellable = true)
    private void drawScreen(int mouseX, int mouseY, float par3, CallbackInfo ci) {
        if (EmiConfig.effectLocation == EffectLocation.TOP) {
            drawCenteredEffects(mouseX, mouseY);
            ci.cancel();
        } else {
            boolean wide = !EmiConfig.effectLocation.compressed;
            this.drawMITEEffectTooltip(mouseX, mouseY, wide);
        }
    }

    @WrapOperation(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/InventoryEffectRenderer;drawCurseBoxTooltip(II)V"))
    private void disableVanillaCurseTooltip(InventoryEffectRenderer instance, int mouseX, int mouseY, Operation<Void> original) {}

    @WrapOperation(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/InventoryEffectRenderer;drawInsulinResistantBoxTooltip(II)V"))
    private void disableVanillaInsulinTooltip(InventoryEffectRenderer instance, int mouseX, int mouseY, Operation<Void> original) {}

    @WrapOperation(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/InventoryEffectRenderer;drawMalnourishedBoxTooltip(II)V"))
    private void disableVanillaMalnourishedTooltip(InventoryEffectRenderer instance, int mouseX, int mouseY, Operation<Void> original) {}

    @Inject(method = "displayDebuffEffects", at = @At("HEAD"), cancellable = true)
    private void displayDebuffEffects(CallbackInfo ci) {
        ci.cancel();

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
        this.debuffY = this.guiTop;
        int debuffX = changeEffectSpace(this.guiLeft - 124);
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

            this.drawMITEEffects(debuffX, spacing, wide);

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

    private void drawMITEEffects(int debuffX, int spacing, boolean wide) {
        EmiDrawContext context = EmiDrawContext.instance();
        if (this.mc.thePlayer.isMalnourished()) {
            context.resetColor();
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            context.drawTexture(MITE_icons, debuffX + 6, debuffY + 7, 18, 198, 18, 18);
            if (wide) {
                context.drawText(Text.translatable("effect.malnourished"), debuffX + 10 + 18 - 1, debuffY + 6 + 1);
                Text description = ((int) this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0 ? Text.translatable("effect.malnourished.slowHealing") : Text.translatable("effect.malnourished.plus50PercentHunger");
                context.drawText(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 0xAAAAAA);
            }
            debuffY += spacing;
        }

        if (this.mc.thePlayer.isInsulinResistant()) {
            context.resetColor();
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            this.mc.getTextureManager().bindTexture(sugar_icon);
            this.drawTexturedModalRect2(debuffX + 7, debuffY + 8, 16, 16);
            EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
            context.setColor(insulin_resistance_level.getRedAsFloat(), insulin_resistance_level.getGreenAsFloat(), insulin_resistance_level.getBlueAsFloat(), 1.0F);
            context.drawTexture(MITE_icons, debuffX + 6, debuffY + 7, 54, 198, 18, 18);
            context.resetColor();
            if (wide) {
                context.drawText(Text.translatable("effect.insulinResistance"), debuffX + 10 + 18 - 1, debuffY + 6 + 1);
                Text description = Text.literal(StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance()));
                context.drawText(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 0xAAAAAAAA);
            }
            debuffY += spacing;
        }

        if (this.mc.thePlayer.is_cursed) {
            context.resetColor();
            this.mc.getTextureManager().bindTexture(field_110408_a);
            drawStatusEffectBackgrounds(debuffX, debuffY, wide);
            context.drawTexture(MITE_icons, debuffX + 6, debuffY + 7, 0, 198, 18, 18);
            if (wide) {
                context.drawText(Text.translatable("effect.cursed"), debuffX + 10 + 18 - 1, debuffY + 6 + 1);
                Text description = this.mc.thePlayer.curse_effect_known ? Text.literal(this.mc.thePlayer.getCurse().getTitle()).formatted(Formatting.DARK_PURPLE) : Text.translatable("curse.unknown");
                context.drawText(description, debuffX + 10 + 18 - 1, debuffY + 6 + 10 + 1, 0xAAAAAAAA);
            }
            debuffY += spacing;
        }
    }
    
    private void drawMITEEffectTooltip(int mouseX, int mouseY, boolean wide) {
        int malnourished_box_left = changeEffectSpace(this.guiLeft - 128);
        int malnourished_box_top = EmiConfig.effectLocation == EffectLocation.TOP ? this.guiTop - 33 : this.guiTop;
        int malnourished_box_right = malnourished_box_left + (wide ? 123 : 31);
        int malnourished_box_bottom = malnourished_box_top + 31;
        if (EmiConfig.effectLocation == EffectLocation.TOP && ((Object) this) instanceof GuiContainerCreative) {
            malnourished_box_top -= 28;
            if (((Object) this) instanceof GuiContainerCreative && EmiAgnos.isModLoaded("rusted_iron_core")) {
                malnourished_box_top -= 22;
            }
        }
        if (this.mc.thePlayer.isMalnourished() && mouseX >= malnourished_box_left && mouseX <= malnourished_box_right && mouseY >= malnourished_box_top && mouseY <= malnourished_box_bottom) {
            TooltipComponent name = TooltipComponent.of(Text.translatable("effect.malnourished"));
            TooltipComponent description_0 = TooltipComponent.of(Text.translatable("effect.malnourished.general").formatted(Formatting.GRAY));
            TooltipComponent description_1 = TooltipComponent.of(Text.translatable("effect.malnourished." + (this.mc.thePlayer.is_malnourished_in_protein ? "protein" : "phytonutrients")).formatted(Formatting.GRAY));
            List<TooltipComponent> tooltip = new ArrayList<>();
            if (!wide) {
                tooltip.add(name);
                tooltip.add(TooltipComponent.of((((int) this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0) ? Text.translatable("effect.malnourished.slowHealing").formatted(Formatting.GRAY) : Text.translatable("effect.malnourished.plus50PercentHunger").formatted(Formatting.GRAY)));
            }
            tooltip.addAll(Arrays.asList(description_0, description_1));
            EmiRenderHelper.drawTooltip(ReflectHelper.dyCast(this), EmiDrawContext.instance(), tooltip, mouseX, Math.max(mouseY, 16));
        }

        int insulin_box_left = changeEffectSpace(this.guiLeft - 128);
        int insulin_box_top = EmiConfig.effectLocation == EffectLocation.TOP ? this.guiTop - 33 : this.guiTop;
        int insulin_box_right = insulin_box_left + (wide ? 123 : 31);
        int insulin_box_bottom = insulin_box_top + 31;
        if (this.mc.thePlayer.isMalnourished()) {
            if (EmiConfig.effectLocation == EffectLocation.TOP) {
                insulin_box_left += 33;
                insulin_box_right += 33;
            } else {
                insulin_box_top += 33;
                insulin_box_bottom += 33;
            }
        }
        if (EmiConfig.effectLocation == EffectLocation.TOP && ((Object) this) instanceof GuiContainerCreative) {
            insulin_box_top -= 28;
            if (((Object) this) instanceof GuiContainerCreative && EmiAgnos.isModLoaded("rusted_iron_core")) {
                insulin_box_top -= 22;
            }
        }
        if (this.mc.thePlayer.isInsulinResistant() && mouseX >= insulin_box_left && mouseX <= insulin_box_right && mouseY >= insulin_box_top && mouseY <= insulin_box_bottom) {
            EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
            TooltipComponent name = TooltipComponent.of(Text.translatable("effect.insulinResistance"));
            TooltipComponent description = TooltipComponent.of(Text.translatable("effect.insulinResistance." + insulin_resistance_level.getUnlocalizedName()).formatted(Formatting.GRAY));
            List<TooltipComponent> tooltip = new ArrayList<>();
            if (!wide) {
                tooltip.add(name);
                tooltip.add(TooltipComponent.of(Text.literal(StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance())).formatted(Formatting.GRAY)));
            }
            tooltip.add(description);
            EmiRenderHelper.drawTooltip(ReflectHelper.dyCast(this), EmiDrawContext.instance(), tooltip, mouseX, Math.max(mouseY, 16));
        }

        int cursed_box_left = changeEffectSpace(this.guiLeft - 128);
        int cursed_box_top = EmiConfig.effectLocation == EffectLocation.TOP ? this.guiTop - 33 : this.guiTop;
        int cursed_box_right = cursed_box_left + (wide ? 123 : 31);
        int cursed_box_bottom = cursed_box_top + 31;
        if (this.mc.thePlayer.isMalnourished()) {
            if (EmiConfig.effectLocation == EffectLocation.TOP) {
                cursed_box_left += 33;
                cursed_box_right += 33;
            } else {
                cursed_box_top += 33;
                cursed_box_bottom += 33;
            }
        }
        if (this.mc.thePlayer.isInsulinResistant()) {
            if (EmiConfig.effectLocation == EffectLocation.TOP) {
                cursed_box_left += 33;
                cursed_box_right += 33;
            } else {
                cursed_box_top += 33;
                cursed_box_bottom += 33;
            }
        }
        if (EmiConfig.effectLocation == EffectLocation.TOP && ((Object) this) instanceof GuiContainerCreative) {
            cursed_box_top -= 28;
            if (((Object) this) instanceof GuiContainerCreative && EmiAgnos.isModLoaded("rusted_iron_core")) {
                cursed_box_top -= 22;
            }
        }
        if (this.mc.thePlayer.is_cursed && mouseX >= cursed_box_left && mouseX <= cursed_box_right && mouseY >= cursed_box_top && mouseY <= cursed_box_bottom) {
            Curse curse = Curse.cursesList[this.mc.thePlayer.curse_id];
            if (this.mc.thePlayer.curse_effect_known) {
                List<TooltipComponent> tooltip = new ArrayList<>();
                if (!wide) {
                    tooltip.add(TooltipComponent.of(Text.translatable("effect.cursed")));
                    tooltip.add(TooltipComponent.of(Text.literal(this.mc.thePlayer.curse_effect_known ? EnumChatFormatting.DARK_PURPLE + this.mc.thePlayer.getCurse().getTitle() : Translator.get("curse.unknown"))));
                }
                for (String s : curse.getTooltip()) {
                    Text text = Text.literal(s).formatted(Formatting.GRAY);
                    tooltip.add(TooltipComponent.of(text));
                }
                EmiRenderHelper.drawTooltip(ReflectHelper.dyCast(this), EmiDrawContext.instance(), tooltip, mouseX, Math.max(mouseY, 16));

            }
        }
    }

    private void drawCenteredEffects(int mouseX, int mouseY) {
        EmiDrawContext context = EmiDrawContext.instance();
        context.resetColor();
        Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
        int size = effects.size();
        if (this.mc.thePlayer.isMalnourished()) ++size;
        if (this.mc.thePlayer.isInsulinResistant()) ++size;
        if (this.mc.thePlayer.is_cursed) ++size;
        if (size == 0) {
            return;
        }
        boolean wide = size == 1;
        int y = this.guiTop - 34;
        if (((Object) this) instanceof GuiContainerCreative) {
            y -= 28;
            if (((Object) this) instanceof GuiContainerCreative && (EmiAgnos.isForge() || GlacierCreativeTabs.newCreativeTabArray.size() > 12)) {
                y -= 22;
            }
        }
        int xOff = 34;
        if (wide) {
            xOff = 122;
        } else if (size > 5) {
            xOff = (this.xSize - 32) / (size - 1);
        }
        int width = (size - 1) * xOff + (wide ? 120 : 32);
        int x = this.guiLeft + (this.xSize - width) / 2;
        PotionEffect hovered = null;
        int restoreY = this.ySize;
        try {
            this.ySize = y;
            //MITE Effects
            if (this.mc.thePlayer.isMalnourished() || this.mc.thePlayer.is_cursed || this.mc.thePlayer.isInsulinResistant()) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
            }
            if (this.mc.thePlayer.isMalnourished()) {
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);
                this.mc.getTextureManager().bindTexture(MITE_icons);
                this.drawTexturedModalRect(x + 6, y + 7, 18, 198, 18, 18);
                if (wide) {
                    context.drawText(Text.translatable("effect.malnourished"), x + 10 + 18 - 1, y + 6 + 1);
                    Text description = ((int) this.mc.theWorld.getTotalWorldTime() - this.initial_tick) / 100 % 2 == 0 ? Text.translatable("effect.malnourished.slowHealing") : Text.translatable("effect.malnourished.plus50PercentHunger");
                    context.drawText(description, x + 10 + 18 - 1, y + 6 + 10 + 1, 0xAAAAAA);
                }
                x += xOff;
            }

            if (this.mc.thePlayer.isInsulinResistant()) {
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);
                this.mc.getTextureManager().bindTexture(sugar_icon);
                this.drawTexturedModalRect2(x + 7, y + 8, 16, 16);
                EnumInsulinResistanceLevel insulin_resistance_level = this.mc.thePlayer.getInsulinResistanceLevel();
                context.setColor(insulin_resistance_level.getRedAsFloat(), insulin_resistance_level.getGreenAsFloat(), insulin_resistance_level.getBlueAsFloat(), 1.0F);
                context.drawTexture(MITE_icons, x + 6, y + 7, 54, 198, 18, 18);
                context.resetColor();
                if (wide) {
                    context.drawText(Text.translatable("effect.insulinResistance"), x + 10 + 18 - 1, y + 6 + 1);
                    Text description = Text.literal(StringUtils.ticksToElapsedTime(this.mc.thePlayer.getInsulinResistance()));
                    context.drawText(description, x + 10 + 18 - 1, y + 6 + 10 + 1, 0xAAAAAAAA);
                }
                x += xOff;
            }

            if (this.mc.thePlayer.is_cursed) {
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);
                context.drawTexture(MITE_icons, x + 6, y + 7, 0, 198, 18, 18);
                if (wide) {
                    context.drawText(Text.translatable("effect.cursed"), x + 10 + 18 - 1, y + 6 + 1);
                    Text description = this.mc.thePlayer.curse_effect_known ? Text.literal(this.mc.thePlayer.getCurse().getTitle()).formatted(Formatting.DARK_PURPLE) : Text.translatable("curse.unknown");
                    context.drawText(description, x + 10 + 18 - 1, y + 6 + 10 + 1, 0xAAAAAAAA);
                }
                x += xOff;
            }
            if (this.mc.thePlayer.isMalnourished() || this.mc.thePlayer.is_cursed || this.mc.thePlayer.isInsulinResistant()) {
                GL11.glPopMatrix();
            }
            for (PotionEffect inst : effects) {
                int ew = wide ? 120 : 32;
                GL11.glPushMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.getTextureManager().bindTexture(field_110408_a);
                drawStatusEffectBackgrounds(x, y, wide);

                drawPotionIcon(x, y, inst);
                if (mouseX >= x && mouseX < x + ew && mouseY >= y && mouseY < y + 32) {
                    hovered = inst;
                }
                x += xOff;
                drawStatusEffectDescriptions(x - width, y, inst, wide);
                GL11.glPopMatrix();
            }
        } finally {
            this.ySize = restoreY;
        }
        if (size > 1) {
            renderTooltip(mouseX, mouseY, hovered);
//            this.drawMITEEffectTooltip(mouseX, mouseY, wide);
        }
    }

    @Unique
    private void renderTooltip(int mouseX, int mouseY, PotionEffect effect) {
        if (effect != null) {
            String amplifier = getPotionAmplifier(effect);
            TooltipComponent name = TooltipComponent.of(Text.translatable(effect.getEffectName()).append(Text.literal(amplifier)));
            TooltipComponent duration = TooltipComponent.of(Text.literal(Potion.getDurationString(effect)));
            EmiRenderHelper.drawTooltip(ReflectHelper.dyCast(this), EmiDrawContext.instance(), List.of(name, duration), mouseX, Math.max(mouseY, 16));
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
            this.drawTexturedModalRect(x + 6, y + 7, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
        }
//        if (EmiAgnos.isModLoaded("rusted_iron_core")) {
//            try {
                if (((moddedmite.rustedironcore.api.interfaces.IPotion) potionType).ric$UsesIndividualTexture()) {
                    this.mc.getTextureManager().bindTexture(((moddedmite.rustedironcore.api.interfaces.IPotion) potionType).ric$GetTexture());
                    this.drawTexturedModalRect2(x + 6, y + 7, 18, 18);
                }
//            } catch (Exception e) {
//                EmiLog.error(e.getMessage(), e);
//            }
//        }
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
