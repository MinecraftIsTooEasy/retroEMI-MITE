package emi.dev.emi.emi.api.stack.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.runtime.EmiLog;
import emi.shims.java.net.minecraft.nbt.StringNbtReader;
import emi.shims.java.net.minecraft.util.JsonHelper;
import net.minecraft.NBTTagCompound;
import net.minecraft.ResourceLocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface EmiStackSerializer<T extends EmiStack> extends EmiIngredientSerializer<T> {
	static final Pattern STACK_REGEX = Pattern.compile("^([\\w_\\-./]+):([\\w_\\-.]+):([\\w_\\-./]+)(\\{.*\\})?$");
	
	EmiStack create(ResourceLocation id, NBTTagCompound nbt, long amount);
	
	@Override
	default EmiIngredient deserialize(JsonElement element) {
		ResourceLocation id = null;
		String nbt = null;
		long amount = 1;
		float chance = 1;
		EmiStack remainder = EmiStack.EMPTY;
		if (JsonHelper.isString(element)) {
			String s = element.getAsString();
			Matcher m = STACK_REGEX.matcher(s);
			if (m.matches()) {
				id = new ResourceLocation(m.group(2), m.group(3));
				nbt = m.group(4);
			}
		}
		else if (element.isJsonObject()) {
			JsonObject json = element.getAsJsonObject();
			id = new ResourceLocation(JsonHelper.getString(json, "id"));
			nbt = JsonHelper.getString(json, "nbt", null);
			amount = JsonHelper.getLong(json, "amount", 1);
			chance = JsonHelper.getFloat(json, "chance", 1);
			if (JsonHelper.hasElement(json, "remainder")) {
				EmiIngredient ing = EmiIngredientSerializer.getDeserialized(json.get("remainder"));
				if (ing instanceof EmiStack stack) {
					remainder = stack;
				}
			}
		}
		if (id != null) {
			try {
				NBTTagCompound nbtComp = null;
				if (nbt != null) {
					nbtComp = StringNbtReader.parse(nbt);
				}
				EmiStack stack = create(id, nbtComp, amount);
				if (chance != 1) {
					stack.setChance(chance);
				}
				if (!remainder.isEmpty()) {
					stack.setRemainder(remainder);
				}
				return stack;
			}
			catch (Exception e) {
				EmiLog.error("Error parsing NBT in deserialized stack");
				e.printStackTrace();
				return EmiStack.EMPTY;
			}
		}
		return EmiStack.EMPTY;
	}
	
	@Override
	default JsonElement serialize(T stack) {
		if (stack.getAmount() == 1 && stack.getChance() == 1 && stack.getRemainder().isEmpty()) {
			String s = getType() + ":" + stack.getId();
			if (stack.hasNbt()) {
				s += StringNbtReader.encode(stack.getNbt());
			}
			return new JsonPrimitive(s);
		}
		else {
			JsonObject json = new JsonObject();
			json.addProperty("type", getType());
			json.addProperty("id", stack.getId().toString());
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
	}
}
