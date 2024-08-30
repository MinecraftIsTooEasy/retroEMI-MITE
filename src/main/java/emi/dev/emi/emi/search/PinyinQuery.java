package emi.dev.emi.emi.search;

import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.pinyin.PinyinMatch;
import emi.shims.java.net.minecraft.text.Text;

public class PinyinQuery extends Query {
    private final String name;

    public PinyinQuery(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean matches(EmiStack stack) {
        return PinyinMatch.contains(getText(stack).getString(), name);
    }

//    @Override
//    public boolean matchesUnbaked(EmiStack stack) {
//        return PinyinMatch.toPinyin(getText(stack).getString()).contains(name);
//    }

    public static Text getText(EmiStack stack) {
        return stack.getName();
    }
}
