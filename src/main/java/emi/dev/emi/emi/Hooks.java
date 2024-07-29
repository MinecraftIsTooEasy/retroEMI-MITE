package emi.dev.emi.emi;

import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.dev.emi.emi.runtime.EmiSidebars;
import emi.dev.emi.emi.screen.EmiScreenManager;
import emi.dev.emi.emi.search.EmiSearch;
import emi.mitemod.emi.api.EMICraftingManager;
import emi.mitemod.emi.api.EMIGuiContainerCreative;
import emi.mitemod.emi.api.EMIMinecraft;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.*;

import java.util.List;

public class Hooks {
	static Minecraft minecraft = Minecraft.getMinecraft();

	//GuiContainer
	public static void renderBackground(int par1, int par2) {
		EmiDrawContext context = EmiDrawContext.instance();
		EmiScreenManager.drawBackground(context, par1, par2, minecraft.timer.renderPartialTicks);
	}

	public static void renderForegroundPre(int par1, int par2, Minecraft mc) {
		GuiContainer screen = (GuiContainer) mc.currentScreen;
		EmiDrawContext context = EmiDrawContext.instance();
		context.push();
		context.matrices().translate(-((EMIGuiContainerCreative)screen).getGuiLeft(), -((EMIGuiContainerCreative)screen).getGuiTop(), 0.0);
		EmiScreenManager.render(context, par1, par2, minecraft.timer.renderPartialTicks);
		context.pop();
	}

	public static void renderForegroundPost(int par1, int par2, Minecraft mc) {
		GuiContainer screen = (GuiContainer) mc.currentScreen;
		EmiDrawContext context = EmiDrawContext.instance();
		context.push();
		context.matrices().translate(-((EMIGuiContainerCreative)screen).getGuiLeft(), -((EMIGuiContainerCreative)screen).getGuiTop(), 0.0);
		EmiScreenManager.drawForeground(context, par1, par2, minecraft.timer.renderPartialTicks);
		context.pop();
	}

	public static void drawSlot(Slot slot) {
		EmiDrawContext context = EmiDrawContext.instance();
		if (EmiScreenManager.search.highlight) {
			EmiSearch.CompiledQuery query = EmiSearch.compiledQuery;
			if (query != null && !query.test(EmiStack.of(slot.getStack()))) {
				context.push();
				context.matrices().translate(0, 0, 300);
				context.fill(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, 18, 18, 0x77000000);
				context.pop();
			}
		}
	}
	//SlotCrafting
	public static void onCrafting(EntityPlayer thePlayer, IInventory craftMatrix) {
		World world = thePlayer.worldObj;
		if (world.isRemote) {
			try {
				InventoryCrafting inv = (InventoryCrafting) craftMatrix;
				Minecraft client = Minecraft.getMinecraft();
				List<IRecipe> list = ((EMICraftingManager)CraftingManager.getInstance()).getRecipes();
				for (var r : list) {
					if (r.matches(inv, client.theWorld)) {
						ResourceLocation id = new SyntheticIdentifier(r);
						EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
						if (recipe != null) {
							EmiSidebars.craft(recipe);
							return;
						}
					}
				}
			} catch (Throwable t) {}
		}
	}

	/**
	 * @see FontRenderer
	 */


}
