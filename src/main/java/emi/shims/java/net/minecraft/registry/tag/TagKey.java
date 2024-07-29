package emi.shims.java.net.minecraft.registry.tag;

import java.util.List;

import net.minecraft.ResourceLocation;

public interface TagKey<T> {
	
	ResourceLocation id();
	List<T> get();
	String getFlavor();
	
}
