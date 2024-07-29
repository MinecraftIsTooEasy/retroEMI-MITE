package emi.dev.emi.emi.registry;

import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.stack.EmiStackInteraction;
import emi.mitemod.emi.api.EMISlotCrafting;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiStackProvidersClientOnly {
	@Nullable
	public static EmiStackInteraction getEmiStackInteraction(Slot s, ItemStack stack) {
		if (s instanceof SlotCrafting craf) {
			// Emi be making assumptions
			try {
				InventoryCrafting inv = (InventoryCrafting) ((EMISlotCrafting) craf).getCraftMatrix();
				Minecraft client = Minecraft.getMinecraft();
				List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
				for (var r : list) {
					if (r.matches(inv, client.theWorld)) {
						ResourceLocation id = new SyntheticIdentifier(r);
						EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
						if (recipe != null) {
							return new EmiStackInteraction(EmiStack.of(stack), recipe, false);
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
}
