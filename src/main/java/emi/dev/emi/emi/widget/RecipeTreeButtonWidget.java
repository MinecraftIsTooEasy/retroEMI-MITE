package emi.dev.emi.emi.widget;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.bom.BoM;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class RecipeTreeButtonWidget extends RecipeButtonWidget {
	
	public RecipeTreeButtonWidget(int x, int y, EmiRecipe recipe) {
		super(x, y, 36, 0, recipe);
	}
	
	@Override
	public int getTextureOffset(int mouseX, int mouseY) {
		int v = super.getTextureOffset(mouseX, mouseY);
		if (BoM.tree != null && BoM.tree.goal.recipe == recipe) {
			v += 36;
		}
		return v;
	}
	
	@Override
	public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
		return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("tooltip.emi.view_tree"))));
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		BoM.setGoal(recipe);
		this.playButtonSound();
		EmiApi.viewRecipeTree();
		return true;
	}
}
