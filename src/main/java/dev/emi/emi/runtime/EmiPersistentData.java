package dev.emi.emi.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.platform.EmiAgnos;
import shims.java.net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class EmiPersistentData {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public static void save() {
		try {
			File file = getPersistentFile();
			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
			JsonObject json = new JsonObject();
			json.add("favorites", EmiFavorites.save());
			EmiSidebars.save(json);
			json.add("recipe_defaults", BoM.saveAdded());
			json.add("hidden_stacks", EmiHidden.save());
			FileWriter writer = new FileWriter(file);
			GSON.toJson(json, writer);
			writer.close();
		} catch (Exception e) {
			EmiLog.error("Failed to write persistent data", e);
		}
	}

	public static void load() {
		File file = getLoadFile();
		if (!file.exists()) {
			return;
		}
		try {
			JsonObject json = GSON.fromJson(new FileReader(file), JsonObject.class);
			if (JsonHelper.hasArray(json, "favorites")) {
				EmiFavorites.load(JsonHelper.getArray(json, "favorites"));
			}
			EmiSidebars.load(json);
			if (JsonHelper.hasJsonObject(json, "recipe_defaults")) {
				BoM.loadAdded(JsonHelper.getObject(json, "recipe_defaults"));
			}
			if (JsonHelper.hasArray(json, "hidden_stacks")) {
				EmiHidden.load(JsonHelper.getArray(json, "hidden_stacks"));
			}
		} catch (Exception e) {
			EmiLog.error("Failed to parse persistent data", e);
		}
	}

	private static File getLoadFile() {
		File file = getPersistentFile();
		if (file.exists()) {
			return file;
		}
		return getLegacyFile();
	}

	private static File getPersistentFile() {
		return new File(EmiAgnos.getConfigDirectory().toFile(), "emi.json");
	}

	private static File getLegacyFile() {
		return new File("emi.json");
	}
}
