package cn.iamtudou.kit;

public class StrKit {

    /**
     * 非空判断
     * @param context 文本对象
     * @return 判断结果 true(空)/false(非空)
     */
    public static boolean isBlank(String context) {
        if (context == null)
            return true;
        else if (context.trim().length() == 0)
            return true;

        return false;
    }
}
