package cn.iamtudou.kit;

import cn.iamtudou.entity.HttpEntity;
import cn.iamtudou.entity.ProxyAuth;
import cn.iamtudou.entity.ProxyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

public class Request {
    static Logger LOG = LoggerFactory.getLogger(Request.class);

    public static HttpEntity get(String url) {
        return baseGet(url, null, null, null);
    }

    public static HttpEntity get(String url, Charset charset) {
        return baseGet(url, null, null, charset);
    }

    public static HttpEntity get(String url, Map<String, String> header, Charset charset) {
        return baseGet(url, null, header, charset);
    }

    public static HttpEntity get(String url, ProxyEntity proxy, Charset charset) {
        return baseGet(url, proxy, null, charset);
    }

    public static HttpEntity get(String url, ProxyEntity proxy, Map<String, String> header, Charset charset) {
        return baseGet(url, proxy, header, charset);
    }

    private static HttpEntity baseGet(String url, ProxyEntity proxyEntity, Map<String, String> header, Charset charset) {
        if (StrKit.isBlank(url)) {
            LOG.warn("request url was empty! get method is not executed.");
            return null;
        }

        String html = null;
        URL netUrl = null;
        HttpEntity httpEntity = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        String charsetStr = "UTF-8";

        if (charset != null)
            // 指定字符编码
            charsetStr = charset.name();
        try {
            netUrl = new URL(url);

            if (proxyEntity != null) {
                //指定使用代理
                InetSocketAddress addr = new InetSocketAddress(proxyEntity.getHost(), proxyEntity.getPort());
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

                if (!StrKit.isBlank(proxyEntity.getAccount()))
                    //使用带用户和密码认证的代理
                    Authenticator.setDefault(new ProxyAuth(proxyEntity.getAccount(), proxyEntity.getPasswd()));

                conn = (HttpURLConnection) netUrl.openConnection(proxy);
            } else
                conn = (HttpURLConnection) netUrl.openConnection();
            if (header != null) {
                //指定使用header参数
                for (Map.Entry<String, String> entry : header.entrySet())
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            inputStream = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
                baos.flush();
            }
            html = baos.toString(charsetStr);
            httpEntity = new HttpEntity(url, conn.getURL().getHost(), conn.getResponseCode(), html);
        } catch (IOException e) {
            LOG.error("", e);
        } finally {
            baos = null;
            inputStream = null;
            conn.disconnect();
            netUrl = null;
            proxyEntity = null;
            header.clear();
        }

        return httpEntity;
    }

    public static HttpEntity post(String url, String dataStr) {
        return null;
    }

    private static HttpEntity basePost(String url, String dataStr) {

        return null;
    }
}
