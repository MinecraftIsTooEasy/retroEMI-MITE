package emi.shims.java.net.minecraft.util;

import net.minecraft.ResourceLocation;

public class NumericIdentifier extends ResourceLocation {

	private final int id;
	
	public NumericIdentifier(int id) {
		super("id", Integer.toString(id));
		this.id = id;
	}

	@Override @Deprecated
	public String getResourceDomain() {
		return super.getResourceDomain();
	}
	
	@Override @Deprecated
	public String getResourcePath() {
		return super.getResourcePath();
	}
	
	public int getId() {
		return id;
	}
	
}
