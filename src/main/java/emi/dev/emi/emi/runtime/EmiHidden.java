package emi.dev.emi.emi.runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import emi.dev.emi.emi.api.stack.Comparison;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import emi.dev.emi.emi.registry.EmiStackList;

import java.util.LinkedHashSet;
import java.util.Set;

public class EmiHidden {
	public static Set<EmiIngredient> hiddenStacks = new LinkedHashSet<>();
	
	public static JsonArray save() {
		JsonArray arr = new JsonArray();
		for (EmiIngredient stack : hiddenStacks) {
			JsonElement el = EmiIngredientSerializer.getSerialized(stack);
			if (el != null && !el.isJsonNull()) {
				arr.add(el);
			}
		}
		return arr;
	}
	
	public static void load(JsonArray arr) {
		hiddenStacks.clear();
		for (JsonElement el : arr) {
			EmiIngredient stack = EmiIngredientSerializer.getDeserialized(el).copy();
			if (!stack.isEmpty()) {
				for (EmiStack es : stack.getEmiStacks()) {
					es.comparison(c -> Comparison.compareNbt());
				}
				hiddenStacks.add(stack);
			}
		}
		EmiStackList.bakeFiltered();
	}
	
	public static boolean isHidden(EmiIngredient stack) {
		return hiddenStacks.contains(stack);
	}
	
	public static void setVisibility(EmiIngredient stack, boolean hide, boolean similar) {
		if (similar && stack.getEmiStacks().size() == 1) {
			EmiStack es = stack.getEmiStacks().get(0);
			for (EmiStack i : EmiStackList.stacks) {
				if (es.getId().equals(i.getId())) {
					if (hide) {
						hiddenStacks.add(i.copy().comparison(c -> Comparison.compareNbt()));
					}
					else {
						hiddenStacks.remove(i);
					}
				}
			}
		}
		else {
			if (hide) {
				stack = stack.copy();
				for (EmiStack es : stack.getEmiStacks()) {
					es.comparison(c -> Comparison.compareNbt());
				}
				hiddenStacks.add(stack);
			}
			else {
				hiddenStacks.remove(stack);
			}
		}
		EmiPersistentData.save();
		EmiStackList.bakeFiltered();
	}
}
