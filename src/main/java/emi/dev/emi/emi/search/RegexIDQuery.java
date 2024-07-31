package emi.dev.emi.emi.search;

import emi.dev.emi.emi.api.stack.EmiStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexIDQuery extends Query {
	private final Pattern pattern;
	
	public RegexIDQuery(String id) {
		Pattern p = null;
		try {
			p = Pattern.compile(id, Pattern.CASE_INSENSITIVE);
		}
		catch (Exception e) {
		}
		pattern = p;
	}
	
	@Override
	public boolean matches(EmiStack stack) {
		if (pattern == null) {
			return false;
		}
		String idString = "";
		if (stack.getItemStack().itemID < 10) {
			idString = "000" + stack.getItemStack().itemID;
		}
		else if (stack.getItemStack().itemID < 100) {
			idString = "00" + stack.getItemStack().itemID;
		}
		else if (stack.getItemStack().itemID < 1000) {
			idString = "0" + stack.getItemStack().itemID;
		}
		else {
			idString = String.valueOf(stack.getItemStack().itemID);
		}
		String id = idString + "/" + stack.getItemStack().getItemSubtype();
		Matcher m = pattern.matcher(id);
		return m.find();
	}
}
