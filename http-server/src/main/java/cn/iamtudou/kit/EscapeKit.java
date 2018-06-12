package cn.iamtudou.kit;

/**
 * 字符转义工具
 */
public class EscapeKit {

    public static final String[] ESCAPE_SPECIAL_CHARS = {
            "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"
    };

    /**
     * 为指定字符添加转义符号
     * @param context
     * @return
     */
    public static String convert(String context) {
        for (int i = 0, length = ESCAPE_SPECIAL_CHARS.length; i < length; i++) {
            context = context.replace(ESCAPE_SPECIAL_CHARS[i], "\\" + ESCAPE_SPECIAL_CHARS[i]);
        }

        return context;
    }
}
