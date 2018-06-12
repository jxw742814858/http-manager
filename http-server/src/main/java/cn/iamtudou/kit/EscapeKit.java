package cn.iamtudou.kit;

public class EscapeKit {

    public static final String[] ESCAPE_SPECIAL_CHARS = {
            "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"
    };

    public static String convert(String context) {
        for (int i = 0, length = ESCAPE_SPECIAL_CHARS.length; i < length; i++) {
            context = context.replace(ESCAPE_SPECIAL_CHARS[i], "\\" + ESCAPE_SPECIAL_CHARS[i]);
        }

        return context;
    }
}
