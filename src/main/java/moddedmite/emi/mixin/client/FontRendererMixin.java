package moddedmite.emi.mixin.client;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import shims.java.com.unascribed.retroemi.REMIMixinHooks;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;

import java.util.Random;

@Mixin(value = FontRenderer.class, priority = 999)
public abstract class FontRendererMixin {
    @Shadow private int textColor;
    @Shadow private float alpha;
    @Shadow private boolean randomStyle;
    @Shadow private boolean boldStyle;
    @Shadow private boolean strikethroughStyle;
    @Shadow private boolean underlineStyle;
    @Shadow private boolean italicStyle;
    @Shadow private float posX;
    @Shadow private float posY;
    @Shadow private float red;
    @Shadow private float blue;
    @Shadow private float green;
    @Shadow public Random fontRandom;
    @Shadow private int[] charWidth;
    @Shadow private boolean unicodeFlag;
    @Shadow public int FONT_HEIGHT;
    @Shadow protected abstract float renderCharAtPos(int par1, char par2, boolean par3);

//    @ModifyConstant(method = {"<init>"}, constant = {@Constant(intValue = 256)})
//    private int modifyChanceTableSize(int val) {
//        return Short.MAX_VALUE;
//    }

    /**
     * @author Xy_Lose
     * @reason Apply custom format codes
     */
    @Overwrite
    private void renderStringAtPos(String par1Str, boolean par2) {
        for (int var3 = 0; var3 < par1Str.length(); ++var3) {
            char var4 = par1Str.charAt(var3);
            int var6;

            if (var4 == 167 && var3 + 1 < par1Str.length()) {
                EnumChatFormatting var12 = EnumChatFormatting.getByChar(par1Str.toLowerCase().charAt(var3 + 1));

                if (var12 == null) {
                    var12 = EnumChatFormatting.WHITE;
                }

                if (var12.isColor()) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (Minecraft.theMinecraft.gameSettings.anaglyph) {
                        var6 = par2 ? var12.rgb_anaglyph_shadow : var12.rgb_anaglyph;
                    } else {
                        var6 = par2 ? var12.rgb_shadow : var12.rgb;
                    }

                    this.textColor = var6;
                    GL11.glColor4f((float) (var6 >> 16) / 255.0F, (float) (var6 >> 8 & 255) / 255.0F, (float) (var6 & 255) / 255.0F, this.alpha);
                    var3 = REMIMixinHooks.applyCustomFormatCodes(ReflectHelper.dyCast(this), par1Str, par2, var3);
                } else if (var12 == EnumChatFormatting.OBFUSCATED) {
                    this.randomStyle = true;
                } else if (var12 == EnumChatFormatting.BOLD) {
                    this.boldStyle = true;
                } else if (var12 == EnumChatFormatting.STRIKETHROUGH) {
                    this.strikethroughStyle = true;
                } else if (var12 == EnumChatFormatting.UNDERLINE) {
                    this.underlineStyle = true;
                } else if (var12 == EnumChatFormatting.ITALIC) {
                    this.italicStyle = true;
                } else if (var12 == EnumChatFormatting.RESET) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
                }

                ++var3;
            } else {
                int var5 = ChatAllowedCharacters.allowedCharacters.indexOf(var4);

                if (this.randomStyle && var5 > 0) {
                    do {
                        var6 = this.fontRandom.nextInt(ChatAllowedCharacters.allowedCharacters.length());
                    }
                    while (this.charWidth[var5 + 32] != this.charWidth[var6 + 32]);

                    var5 = var6;
                }

                float var7 = this.unicodeFlag ? 0.5F : 1.0F;
                boolean var8 = (var5 <= 0 || this.unicodeFlag) && par2;

                if (var8) {
                    this.posX -= var7;
                    this.posY -= var7;
                }

                float var9 = this.renderCharAtPos(var5, var4, this.italicStyle);

                if (var8) {
                    this.posX += var7;
                    this.posY += var7;
                }

                if (this.boldStyle) {
                    this.posX += var7;

                    if (var8) {
                        this.posX -= var7;
                        this.posY -= var7;
                    }

                    this.renderCharAtPos(var5, var4, this.italicStyle);
                    this.posX -= var7;

                    if (var8) {
                        this.posX += var7;
                        this.posY += var7;
                    }

                    ++var9;
                }

                Tessellator var10;

                if (this.strikethroughStyle) {
                    var10 = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var10.startDrawingQuads();
                    var10.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                    var10.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                    var10.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (this.underlineStyle) {
                    var10 = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var10.startDrawingQuads();
                    int var11 = this.underlineStyle ? -1 : 0;
                    var10.addVertex((double) (this.posX + (float) var11), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
                    var10.addVertex((double) (this.posX + (float) var11), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
                    var10.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.posX += (float) ((int) var9);
            }
        }
    }
}
