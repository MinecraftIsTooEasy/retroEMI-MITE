package emi.dev.emi.emi.search;

import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.pinyin.PinyinMatch;

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
        if (pattern == null) {
            return false;
        }
        Matcher m = pattern.matcher(PinyinMatch.toPinyin(PinyinQuery.getText(stack).getString()));
        return m.find();
    }
}
