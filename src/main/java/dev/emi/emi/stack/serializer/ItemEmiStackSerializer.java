package dev.emi.emi.stack.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import shims.java.net.minecraft.util.JsonHelper;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.NBTTagCompound;
import net.minecraft.ResourceLocation;

public class ItemEmiStackSerializer implements EmiStackSerializer<ItemEmiStack> {
	
	@Override
	public String getType() {
		return "item";
	}
	
	@Override
	public EmiIngredient deserialize(JsonElement element) {
		EmiIngredient ing = EmiStackSerializer.super.deserialize(element);
		if (element.isJsonObject() && ing instanceof ItemEmiStack ies) {
			ies.getItemStack().setItemSubtype(JsonHelper.getInt(element.getAsJsonObject(), "meta", 0));
		}
		return ing;
	}
	
	@Override
	public JsonElement serialize(ItemEmiStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("type", getType());
		json.addProperty("id", stack.getId().toString());
		json.addProperty("meta", stack.getItemStack().getItemSubtype());
		if (stack.getAmount() != 1) {
			json.addProperty("amount", stack.getAmount());
		}
		if (stack.getChance() != 1) {
			json.addProperty("chance", stack.getChance());
		}
		if (!stack.getRemainder().isEmpty()) {
			EmiStack remainder = stack.getRemainder();
			if (!remainder.getRemainder().isEmpty()) {
				remainder = remainder.copy().setRemainder(EmiStack.EMPTY);
			}
			if (remainder.getRemainder().isEmpty()) {
				JsonElement remainderElement = EmiIngredientSerializer.getSerialized(remainder);
				if (remainderElement != null) {
					json.add("remainder", remainderElement);
				}
			}
		}
		return json;
	}
	
	@Override
	public EmiStack create(ResourceLocation id, NBTTagCompound nbt, long amount) {
		if (!id.getResourceDomain().equals("id")) {
			return EmiStack.EMPTY;
		}
		ItemStack stack = new ItemStack(Item.itemsList[Integer.parseInt(id.getResourcePath())]);
		stack.setTagCompound(nbt);
		return EmiStack.of(stack, amount);
	}
}
