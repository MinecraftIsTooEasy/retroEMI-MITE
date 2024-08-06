package emi.shims.java.net.minecraft.nbt;

import net.minecraft.CompressedStreamTools;
import net.minecraft.NBTTagCompound;

import java.util.Base64;

public class StringNbtReader {

	public static NBTTagCompound parse(String nbt) {
		if (nbt.isEmpty() || nbt.equals("{}")) return new NBTTagCompound();
		if (nbt.startsWith("{{binnbt:") && nbt.endsWith("}}")) {
            return CompressedStreamTools.decompress(Base64.getDecoder().decode(nbt.substring(9, nbt.length()-2)));
        }
		throw new IllegalArgumentException(nbt + " doesn't look like binnbt (retroEMI does not implement Mojangson/SNBT)");
	}
	
	public static String encode(NBTTagCompound nbt) {
        return "{{binnbt:"+Base64.getEncoder().encodeToString(CompressedStreamTools.compress(nbt))+"}}";
    }

}
