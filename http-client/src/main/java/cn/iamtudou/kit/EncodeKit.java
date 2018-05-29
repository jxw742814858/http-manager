package cn.iamtudou.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodeKit {
    static Logger LOG = LoggerFactory.getLogger(EncodeKit.class);

    /**
     * 将字符串中 中文部分 转换URL编码
     * @param str
     * @return
     */
    public static String encodeCn(String str) {
        try {
            Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(str);
            while (matcher.find()) {
                String tmp = matcher.group();
                str = str.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            }
            return str;
        } catch (UnsupportedEncodingException uee) {
            LOG.error("", uee);
        }

        return null;
    }
}
