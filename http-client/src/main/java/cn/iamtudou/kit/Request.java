package cn.iamtudou.kit;

import entity.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {
    static Logger LOG = LoggerFactory.getLogger(Request.class);

    public static HttpEntity get(String url) {
        if (StrKit.isBlank(url)) {
            LOG.warn("request url was empty! method is not executed.");
            return null;
        }



        return null;
    }
}
