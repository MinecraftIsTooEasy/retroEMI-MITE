package shims.java.net.minecraft.tag;

import net.minecraft.ResourceLocation;

import java.util.List;

public interface TagKey<T> {
	
	ResourceLocation id();
	List<T> get();
	String getFlavor();
	
}
