package moddedmite.emi.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    @Shadow
    private int textColor;

    @Shadow
    private float alpha;

    @Shadow
    private boolean randomStyle;
    @Shadow
    private boolean boldStyle;
    @Shadow
    private boolean strikethroughStyle;
    @Shadow
    private boolean underlineStyle;
    @Shadow
    private boolean italicStyle;
    @Shadow private float posX;
    @Shadow private float posY;

    @Shadow protected abstract float renderCharAtPos(int par1, char par2, boolean par3);

    @Shadow private float red;
    @Shadow private float blue;
    @Shadow private float green;
    @Shadow public Random fontRandom;
    @Shadow private int[] charWidth;
    @Shadow private boolean unicodeFlag;
    @Shadow public int FONT_HEIGHT;
    @Unique
    private int var3_1;

    @ModifyConstant(method = {"<init>"}, constant = {@Constant(intValue = 256)})
    private int modifyChanceTableSize(int val) {
        return Short.MAX_VALUE;
    }

    /**
     * Hook for EMI
     */
    @Unique
    public int applyCustomFormatCodes(String str, boolean shadow, int i) {
        if (str.charAt(i + 1) == 'x') {
            int next = str.indexOf(String.valueOf('\u00a7') + "x", i + 1);
            if (next != -1) {
                String s = str.substring(i + 1, next);
                int color = Integer.parseInt(s.replace(String.valueOf('\u00a7'), "").substring(1), 16);
                if (shadow) {
                    color = (color & 16579836) >> 2 | color & -16777216;
                }
                this.textColor = color;
                GL11.glColor4f((color >> 16) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, this.alpha);
                i += s.length()+1;
            }
        }
        return i;
    }

    @Inject(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void applyCustomFormatCodes(String par1Str, boolean par2, CallbackInfo ci, int var3, char var4, int var6, EnumChatFormatting enum_chat_formatting) {
        var3 = this.applyCustomFormatCodes(par1Str, par2, var3);
    }
//
//    @Inject(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnumChatFormatting;isColor()Z"))
//    private void changeVar3(String par1Str, boolean par2, CallbackInfo ci, @Local LocalIntRef var3) {
////        for (int var3_2 = 0; var3_2 < par1Str.length(); ++var3_2) {
////            char var4 = par1Str.charAt(var3_2);
////            if (var4 == 167 && var3_2 + 1 < par1Str.length()) {
//                var3.set(var3_1);
//            }
////        }
////    }

//    @Overwrite
//    private void renderStringAtPos(String par1Str, boolean par2) {
//        for (int var3 = 0; var3 < par1Str.length(); ++var3) {
//            char var4 = par1Str.charAt(var3);
//            int var6;
//
//            if (var4 == 167 && var3 + 1 < par1Str.length()) {
//                EnumChatFormatting var12 = EnumChatFormatting.getByChar(par1Str.toLowerCase().charAt(var3 + 1));
//
//                if (var12 == null) {
//                    var12 = EnumChatFormatting.WHITE;
//                }
//
//                if (var12.isColor()) {
//                    this.randomStyle = false;
//                    this.boldStyle = false;
//                    this.strikethroughStyle = false;
//                    this.underlineStyle = false;
//                    this.italicStyle = false;
//
//                    if (Minecraft.theMinecraft.gameSettings.anaglyph) {
//                        var6 = par2 ? var12.rgb_anaglyph_shadow : var12.rgb_anaglyph;
//                    } else {
//                        var6 = par2 ? var12.rgb_shadow : var12.rgb;
//                    }
//
//                    this.textColor = var6;
//                    GL11.glColor4f((float) (var6 >> 16) / 255.0F, (float) (var6 >> 8 & 255) / 255.0F, (float) (var6 & 255) / 255.0F, this.alpha);
//                    var3 = applyCustomFormatCodes(par1Str, par2, var3);
//                } else if (var12 == EnumChatFormatting.OBFUSCATED) {
//                    this.randomStyle = true;
//                } else if (var12 == EnumChatFormatting.BOLD) {
//                    this.boldStyle = true;
//                } else if (var12 == EnumChatFormatting.STRIKETHROUGH) {
//                    this.strikethroughStyle = true;
//                } else if (var12 == EnumChatFormatting.UNDERLINE) {
//                    this.underlineStyle = true;
//                } else if (var12 == EnumChatFormatting.ITALIC) {
//                    this.italicStyle = true;
//                } else if (var12 == EnumChatFormatting.RESET) {
//                    this.randomStyle = false;
//                    this.boldStyle = false;
//                    this.strikethroughStyle = false;
//                    this.underlineStyle = false;
//                    this.italicStyle = false;
//                    GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
//                }
//
//                ++var3;
//            } else {
//                int var5 = ChatAllowedCharacters.allowedCharacters.indexOf(var4);
//
//                if (this.randomStyle && var5 > 0) {
//                    do {
//                        var6 = this.fontRandom.nextInt(ChatAllowedCharacters.allowedCharacters.length());
//                    }
//                    while (this.charWidth[var5 + 32] != this.charWidth[var6 + 32]);
//
//                    var5 = var6;
//                }
//
//                float var7 = this.unicodeFlag ? 0.5F : 1.0F;
//                boolean var8 = (var5 <= 0 || this.unicodeFlag) && par2;
//
//                if (var8) {
//                    this.posX -= var7;
//                    this.posY -= var7;
//                }
//
//                float var9 = this.renderCharAtPos(var5, var4, this.italicStyle);
//
//                if (var8) {
//                    this.posX += var7;
//                    this.posY += var7;
//                }
//
//                if (this.boldStyle) {
//                    this.posX += var7;
//
//                    if (var8) {
//                        this.posX -= var7;
//                        this.posY -= var7;
//                    }
//
//                    this.renderCharAtPos(var5, var4, this.italicStyle);
//                    this.posX -= var7;
//
//                    if (var8) {
//                        this.posX += var7;
//                        this.posY += var7;
//                    }
//
//                    ++var9;
//                }
//
//                Tessellator var10;
//
//                if (this.strikethroughStyle) {
//                    var10 = Tessellator.instance;
//                    GL11.glDisable(GL11.GL_TEXTURE_2D);
//                    var10.startDrawingQuads();
//                    var10.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
//                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
//                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
//                    var10.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
//                    var10.draw();
//                    GL11.glEnable(GL11.GL_TEXTURE_2D);
//                }
//
//                if (this.underlineStyle) {
//                    var10 = Tessellator.instance;
//                    GL11.glDisable(GL11.GL_TEXTURE_2D);
//                    var10.startDrawingQuads();
//                    int var11 = this.underlineStyle ? -1 : 0;
//                    var10.addVertex((double) (this.posX + (float) var11), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
//                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
//                    var10.addVertex((double) (this.posX + var9), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
//                    var10.addVertex((double) (this.posX + (float) var11), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
//                    var10.draw();
//                    GL11.glEnable(GL11.GL_TEXTURE_2D);
//                }
//
//                this.posX += (float) ((int) var9);
//            }
//        }
//    }
}
