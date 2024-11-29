package dev.emi.emi.widget;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiScreenshotRecorder;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.ScreenShotHelper;
import org.lwjgl.input.Mouse;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecipeScreenshotButtonWidget extends RecipeButtonWidget {
	public RecipeScreenshotButtonWidget(int x, int y, EmiRecipe recipe) {
		super(x, y, 60, 0, recipe);
	}
	
	@Override
	public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
		return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("tooltip.emi.recipe_screenshot"))));
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		this.playButtonSound();

		ResourceLocation id = recipe.getId();
		String path;
		if (id == null) {
			path = "unknown-recipe";
		} else {
			// Note that saveScreenshot treats `/`s as indicating subdirectories.
			// We don't want to keep `/` in paths because we want all recipe images in consistent directory locations.
			path = id.getResourceDomain() + "/" + id.getResourcePath().replace("/", "_");
		}

		int width = recipe.getDisplayWidth() + 8;
		int height = recipe.getDisplayHeight() + 8;
		Minecraft client = Minecraft.getMinecraft();
		// TODO this saves screenshots of recipes, (todo from RetroEMI)
//		DrawContext context = DrawContext.INSTANCE;
//		EmiScreenshotRecorder.saveScreenshot("emi/recipes/" + path, width, height,
//				() -> EmiRenderHelper.renderRecipe(recipe, EmiDrawContext.wrap(context), 0, 0, false, -1));
		EmiScreenshotRecorder.saveScreenshot(client.mcDataDir, width, height);
        return true;
    }
}
