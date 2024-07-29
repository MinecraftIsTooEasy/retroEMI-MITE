package emi.dev.emi.emi.bom;

import emi.dev.emi.emi.api.stack.EmiIngredient;

public class FlatMaterialCost {
	public EmiIngredient ingredient;
	public long amount;
	
	public FlatMaterialCost(EmiIngredient ingredient, long amount) {
		this.ingredient = ingredient;
		this.amount = amount;
	}
	
	public long getEffectiveAmount() {
		return amount;
	}
}
