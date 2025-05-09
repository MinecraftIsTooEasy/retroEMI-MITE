package dev.emi.emi.search;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.Minecraft;
import net.xiaoyu233.fml.FishModLoader;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPinyinQuery extends Query {
    private final Pattern pattern;

    public RegexPinyinQuery(String name) {
        Pattern p = null;
        try {
            p = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        }
        catch (Exception e) {
        }
        pattern = p;
    }

    @Override
    public boolean matches(EmiStack stack) {
        if (this.pattern == null) {
            return false;
        }
        Matcher m = null;
        if (m == null)
            return false;
        if (FishModLoader.hasMod("pinin") && EmiConfig.searchNameByPinyin && Objects.equals(Minecraft.theMinecraft.gameSettings.language, "zh_CN")) {
            try {
                m = this.pattern.matcher(me.towdium.pinin.PinyinMatch.toPinyin(PinyinQuery.getText(stack).getString()));
            }
            catch (Exception ignored) {
            }
        }
        return m.find();
    }
}
