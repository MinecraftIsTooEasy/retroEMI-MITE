package emi.dev.emi.emi.pinyin;

import me.towdium.pinin.DictLoader;
import me.towdium.pinin.PinIn;
import me.towdium.pinin.elements.Char;
import me.towdium.pinin.elements.Pinyin;
import me.towdium.pinin.utils.PinyinFormat;

import java.util.function.BiConsumer;

public class PinyinMatch {

    public static final PinIn context = new PinIn(new Loader()).config().accelerate(true).commit();

    public static boolean contains(String s, CharSequence cs) {
        return context.contains(s, cs.toString());
    }
    public static String toPinyin(String s) {
        StringBuilder pinyin = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            Char ch = context.getChar(s.charAt(i));
            Pinyin py = ch.pinyins()[0];
            pinyin.append(PinyinFormat.RAW.format(py));
        }
        return pinyin.toString();
    }

    static class Loader extends DictLoader.Default {
        @Override
        public void load(BiConsumer<Character, String[]> feed) {
            super.load(feed);
            feed.accept('\u9FCF', new String[]{"mai4"});   // 钅麦
            feed.accept('\u9FD4', new String[]{"ge1"});    // 钅哥
            feed.accept('\u9FED', new String[]{"ni3"});    // 钅尔
            feed.accept('\u9FEC', new String[]{"tian2"});  // 石田
            feed.accept('\u9FEB', new String[]{"ao4"});    // 奥气
            feed.accept('\uE900', new String[]{"lu2"});    // 钅卢
            feed.accept('\uE901', new String[]{"du4"});    // 钅杜
            feed.accept('\uE902', new String[]{"xi3"});    // 钅喜
            feed.accept('\uE903', new String[]{"bo1"});    // 钅波
            feed.accept('\uE904', new String[]{"hei1"});   // 钅黑
            feed.accept('\uE906', new String[]{"da2"});    // 钅达
            feed.accept('\uE907', new String[]{"lun2"});   // 钅仑
            feed.accept('\uE910', new String[]{"fu1"});    // 钅夫
            feed.accept('\uE912', new String[]{"li4"});    // 钅立
        }
    }
}

