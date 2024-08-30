package dev.emi.emi.data;

import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourceManagerReloadListener;

import java.util.List;

public class EmiRemoveFromIndex implements EmiResourceReloadListener, ResourceManagerReloadListener {
	
	public static List<EmiIngredient> removed = Lists.newArrayList();
	public static List<IndexStackData.Added> added = Lists.newArrayList();
	public static IndexStackData entries;
	private static final ResourceLocation ID = new ResourceLocation("emi", "removed_stacks");
	
	@Override
	public void onResourceManagerReload(ResourceManager var1) {
		entries = new IndexStackData(added, removed);
	}
	
	@Override
	public ResourceLocation getEmiId() {
		return ID;
	}
}
