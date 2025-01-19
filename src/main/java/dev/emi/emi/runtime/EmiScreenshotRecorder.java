package dev.emi.emi.runtime;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import net.minecraft.Minecraft;
import net.minecraft.Tessellator;
import org.joml.Matrix4f;

//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.systems.VertexSorter;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
//import net.minecraft.Framebuffer;
//import net.minecraft.NativeImage;
//import net.minecraft.ClickEvent;
//import net.minecraft.Style;
//import net.minecraft.Text;
import net.minecraft.Util;
import org.joml.Matrix4fStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;

public class EmiScreenshotRecorder {
    private static final String SCREENSHOTS_DIRNAME = "screenshots";

    /**
     * Saves a screenshot to the game's `screenshots` directory, doing the appropriate setup so that anything rendered in renderer will be captured
     * and saved.
     * <p>
     * <b>Note:</b> the path can have <code>/</code> characters, indicating subdirectories. Java handles these correctly on Windows. The path should
     * <b>not</b> contain the <code>.png</code> extension, as that will be added after checking for duplicates. If a file with this path already
     * exists, then path will be suffixed with a <code>_#</code>, before adding the <code>.png</code> extension, where <code>#</code> represents an
     * increasing number to avoid conflicts.
     * <p>
     * <b>Note 2:</b> The width and height parameters are reflected in the viewport when rendering. But the EMI-config
     * <code>ui.recipe-screenshot-scale</code> value causes the resulting image to be scaled.
     *
     * @param path     the path to save the screenshot to, without extension.
     * @param width    the width of the screenshot, not counting EMI-config scale.
     * @param height   the height of the screenshot, not counting EMI-config scale.
     * @param renderer a function to render the things being screenshotted.
     */
//    public static void saveScreenshot(String path, int width, int height, Runnable renderer) {
//        if (!RenderSystem.isOnRenderThread()) {
//            RenderSystem.recordRenderCall(() -> saveScreenshotInner(path, width, height, renderer));
//        } else {
//            saveScreenshotInner(path, width, height, renderer);
//        }
//    }
//
//    private static void saveScreenshotInner(String path, int width, int height, Runnable renderer) {
//        Minecraft client = Minecraft.getMinecraft();
//
//        int scale;
//        if (EmiConfig.recipeScreenshotScale < 1) {
//            scale = EmiPort.getGuiScale(client);
//        } else {
//            scale = EmiConfig.recipeScreenshotScale;
//        }
//
//        Tessellator tessellator = new Tessellator();
////        Framebuffer framebuffer = new SimpleFramebuffer(width * scale, height * scale, true, Minecraft.isRunningOnMac);
//        tessellator.setColorRGBA_F(0f, 0f, 0f, 0f);
//        framebuffer.clear(Minecraft.isRunningOnMac);
//
//        tessellator.draw();
//
//        Matrix4fStack view = RenderSystem.getModelViewStack();
//        view.pushMatrix();
//        view.identity();
//        view.translate(-1.0f, 1.0f, 0.0f);
//        view.scale(2f / width, -2f / height, -1f / 1000f);
//        view.translate(0.0f, 0.0f, 10.0f);
//        RenderSystem.applyModelViewMatrix();
//
//        Matrix4f backupProj = RenderSystem.getProjectionMatrix();
//        RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorter.BY_Z);
//
//        renderer.run();
//
//        RenderSystem.setProjectionMatrix(backupProj, VertexSorter.BY_Z);
//        view.popMatrix();
//        RenderSystem.applyModelViewMatrix();
//
//        framebuffer.endWrite();
//        client.getFramebuffer().beginWrite(true);
//
//        saveScreenshotInner(client.runDirectory, path, framebuffer,
//                message -> client.execute(() -> client.inGameHud.getChatHud().addMessage(message)));
//    }
//
//    private static void saveScreenshotInner(File gameDirectory, String suggestedPath, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
//        NativeImage nativeImage = takeScreenshot(framebuffer);
//
//        File screenshots = new File(gameDirectory, SCREENSHOTS_DIRNAME);
//        screenshots.mkdir();
//
//        String filename = getScreenshotFilename(screenshots, suggestedPath);
//        File file = new File(screenshots, filename);
//
//        // Make sure the parent file exists. Note: `/`s in suggestedPath are valid, as they indicate subdirectories. Java even translates this
//        // correctly on Windows.
//        File parent = file.getParentFile();
//        parent.mkdirs();
//
//        Util.getIoWorkerExecutor().execute(() -> {
//            try {
//                nativeImage.writeTo(file);
//
//                Text text = EmiPort.literal(filename,
//                        Style.EMPTY.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
//                messageReceiver.accept(EmiPort.translatable("screenshot.success", text));
//            } catch (Throwable e) {
//                EmiLog.error("Failed to write screenshot");
//                e.printStackTrace();
//                messageReceiver.accept(EmiPort.translatable("screenshot.failure", e.getMessage()));
//            } finally {
//                nativeImage.close();
//            }
//        });
//    }
//
//    private static NativeImage takeScreenshot(Framebuffer framebuffer) {
//        int i = framebuffer.textureWidth;
//        int j = framebuffer.textureHeight;
//        NativeImage nativeImage = new NativeImage(i, j, false);
//        RenderSystem.bindTexture(framebuffer.getColorAttachment());
//        nativeImage.loadFromTextureImage(0, false);
//        nativeImage.mirrorVertically();
//        return nativeImage;
//    }

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static IntBuffer field_74293_b;
    private static int[] field_74294_c;


    public static String saveScreenshot(File par0File, int par1, int par2) {
        return func_74292_a(par0File, (String)null, par1, par2);
    }

    public static String func_74292_a(File par0File, String par1Str, int par2, int par3) {
        try {
            File var4 = new File(par0File, "screenshots");
            var4.mkdir();
            int var5 = par2 * par3;

            if (field_74293_b == null || field_74293_b.capacity() < var5) {
                field_74293_b = BufferUtils.createIntBuffer(var5);
                field_74294_c = new int[var5];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            field_74293_b.clear();
            GL11.glReadPixels(0, 0, par2, par3, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, field_74293_b);
            field_74293_b.get(field_74294_c);
            func_74289_a(field_74294_c, par2, par3);
            BufferedImage var6 = new BufferedImage(par2, par3, 1);
            var6.setRGB(0, 0, par2, par3, field_74294_c, 0, par2);
            File var7;

            if (par1Str == null) {
                var7 = func_74290_a(var4);
            } else {
                var7 = new File(var4, par1Str);
            }

            ImageIO.write(var6, "png", var7);
            return "Saved screenshot as " + var7.getName();
        } catch (Exception var8) {
            var8.printStackTrace();
            return "Failed to save: " + var8;
        }
    }

    private static File func_74290_a(File par0File) {
        String var2 = dateFormat.format(new Date()).toString();
        int var3 = 1;

        while (true) {
            File var1 = new File(par0File, var2 + (var3 == 1 ? "" : "_" + var3) + ".png");

            if (!var1.exists()) {
                return var1;
            }

            ++var3;
        }
    }

    private static void func_74289_a(int[] par0ArrayOfInteger, int par1, int par2) {
        int[] var3 = new int[par1];
        int var4 = par2 / 2;

        for (int var5 = 0; var5 < var4; ++var5) {
            System.arraycopy(par0ArrayOfInteger, var5 * par1, var3, 0, par1);
            System.arraycopy(par0ArrayOfInteger, (par2 - 1 - var5) * par1, par0ArrayOfInteger, var5 * par1, par1);
            System.arraycopy(var3, 0, par0ArrayOfInteger, (par2 - 1 - var5) * par1, par1);
        }
    }
}

