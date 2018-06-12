package cn.iamtudou.kit;

import cn.iamtudou.constants.HttpConstants;
import cn.iamtudou.entity.HttpEntity;
import cn.iamtudou.entity.ProxyAuth;
import cn.iamtudou.entity.ProxyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

public class Request {

    static Logger LOG = LoggerFactory.getLogger(Request.class);

    public static HttpEntity head(String url) {
        return baseReq(url, null, null, null, null, HttpConstants.REQUEST_METHOD_HEAD);
    }

    public static HttpEntity head(String url, ProxyEntity proxyEntity) {
        return baseReq(url, proxyEntity, null, null, null, HttpConstants.REQUEST_METHOD_HEAD);
    }

    public static HttpEntity get(String url) {
        return baseReq(url, null, null, null, null, HttpConstants.REQUEST_METHOD_GET);
    }

    public static HttpEntity get(String url, Charset charset) {
        return baseReq(url, null, null, null, charset, HttpConstants.REQUEST_METHOD_GET);
    }

    public static HttpEntity get(String url, Map<String, String> header, Charset charset) {
        return baseReq(url, null, header, null, charset, HttpConstants.REQUEST_METHOD_GET);
    }

    public static HttpEntity get(String url, ProxyEntity proxy, Charset charset) {
        return baseReq(url, proxy, null, null, charset, HttpConstants.REQUEST_METHOD_GET);
    }

    public static HttpEntity get(String url, ProxyEntity proxy, Map<String, String> header, Charset charset) {
        return baseReq(url, proxy, header, null, charset, HttpConstants.REQUEST_METHOD_GET);
    }

    public static HttpEntity post(String url, String dataStr) {
        return baseReq(url, null, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity post(String url, Map<String, String> header, String dataStr) {
        return baseReq(url, null, header, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity post(String url, ProxyEntity proxyEntity, String dataStr) {
        return baseReq(url, proxyEntity, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity post(String url, ProxyEntity proxyEntity, Map<String, String> header, String dataStr,
                                  Charset charset) {
        return baseReq(url, proxyEntity, header, dataStr, charset, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity put(String url, String dataStr) {
        return baseReq(url, null, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity put(String url, Map<String, String> header, String dataStr) {
        return baseReq(url, null, header, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity put(String url, ProxyEntity proxyEntity, String dataStr) {
        return baseReq(url, proxyEntity, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity put(String url, ProxyEntity proxyEntity, Map<String, String> header, String dataStr,
                                 Charset charset) {
        return baseReq(url, proxyEntity, header, dataStr, charset, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity patch(String url, String dataStr) {
        return baseReq(url, null, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity patch(String url, Map<String, String> header, String dataStr) {
        return baseReq(url, null, header, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity patch(String url, ProxyEntity proxyEntity, String dataStr) {
        return baseReq(url, proxyEntity, null, dataStr, null, HttpConstants.REQUEST_METHOD_POST);
    }

    public static HttpEntity patch(String url, ProxyEntity proxyEntity, Map<String, String> header, String dataStr,
                                   Charset charset) {
        return baseReq(url, proxyEntity, header, dataStr, charset, HttpConstants.REQUEST_METHOD_POST);
    }

    /**
     * 请求公共实现类
     *
     * @param url
     * @param proxyEntity
     * @param header
     * @param dataStr
     * @param charset
     * @param requestMethod
     * @return
     */
    private static HttpEntity baseReq(String url, ProxyEntity proxyEntity, Map<String, String> header, String dataStr,
                                      Charset charset, String requestMethod) {
        if (StrKit.isBlank(url)) {
            LOG.warn("request url was empty! get method is not executed.");
            return null;
        }
        url = EncodeKit.encodeCn(url);

        String html = null;
        URL netUrl = null;
        HttpEntity httpEntity = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        BufferedReader responseReader = null;
        DataOutputStream dos = null;
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

            //TODO 待验证作用
            conn.setInstanceFollowRedirects(true);

            switch (requestMethod) {
                case HttpConstants.REQUEST_METHOD_HEAD:
                    conn.setRequestMethod("HEAD");
                    break;
                case HttpConstants.REQUEST_METHOD_GET:
                    conn.setRequestMethod("GET");
                    break;
                case HttpConstants.REQUEST_METHOD_POST:
                    conn.setRequestMethod("POST");
                    break;
                case HttpConstants.REQUEST_METHOD_PUT:
                    conn.setRequestMethod("PUT");
                    break;
                case HttpConstants.REQUEST_METHOD_PATCH:
                    conn.setRequestMethod("patch");
                    break;
                default:
                    break;
            }

            switch (requestMethod) {
                case HttpConstants.REQUEST_METHOD_HEAD:
                case HttpConstants.REQUEST_METHOD_GET:
                    inputStream = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(bytes)) != -1) {
                        baos.write(bytes, 0, len);
                        baos.flush();
                    }

                    html = baos.toString(charsetStr);
                    break;
                case HttpConstants.REQUEST_METHOD_POST:
                case HttpConstants.REQUEST_METHOD_PUT:
                case HttpConstants.REQUEST_METHOD_PATCH:
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(dataStr);
                    dos.flush();
                    dos.close();

                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(),
                            charsetStr));
                    while ((line = responseReader.readLine()) != null)
                        sb.append(line).append("\n");

                    html = sb.toString();
                    break;
                default:
                    break;
            }

            httpEntity = new HttpEntity(url, conn.getURL().getHost(), conn.getResponseCode(), html);
        } catch (IOException e) {
            LOG.error("method: {} | url: {} request failed, msg: {}", requestMethod.toUpperCase(), url, e.getMessage());
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (responseReader != null)
                    responseReader.close();
                if (baos != null)
                    baos.close();
                if (dos != null)
                    dos.close();
                conn.disconnect();
                netUrl = null;
                proxyEntity = null;
                if (header != null)
                    header.clear();
            } catch (IOException e) {
                LOG.error("a mistake in closing the resource! {}", e);
            }
        }

        return httpEntity;
    }
}
