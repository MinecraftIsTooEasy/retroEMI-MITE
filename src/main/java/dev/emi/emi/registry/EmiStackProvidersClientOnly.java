package dev.emi.emi.registry;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import moddedmite.emi.api.EMISlotCrafting;
import shims.java.net.minecraft.util.SyntheticIdentifier;
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
