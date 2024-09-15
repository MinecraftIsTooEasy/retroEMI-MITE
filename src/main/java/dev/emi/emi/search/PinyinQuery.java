package dev.emi.emi.search;

import dev.emi.emi.api.stack.EmiStack;
import net.xiaoyu233.fml.FishModLoader;
import shims.java.net.minecraft.text.Text;

public class PinyinQuery extends Query {
    private final String name;

    public PinyinQuery(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean matches(EmiStack stack) {
        boolean contains = false;
        if (FishModLoader.hasMod("pinin")) {
            try {
                contains = me.towdium.pinin.PinyinMatch.contains(PinyinQuery.getText(stack).getString(), (CharSequence)this.name);
            }
            catch (Exception ignored) {
            }
        }
        return contains;
    }

//    @Override
//    public boolean matchesUnbaked(EmiStack stack) {
//        return PinyinMatch.toPinyin(getText(stack).getString()).contains(name);
//    }

    public static Text getText(EmiStack stack) {
        return stack.getName();
    }
}
