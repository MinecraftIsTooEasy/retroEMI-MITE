package emi.shims.java.net.minecraft.registry.tag;

import net.minecraft.ResourceLocation;

import java.util.List;

public interface TagKey<T> {
	
	ResourceLocation id();
	List<T> get();
	String getFlavor();
	
}
