package entity;

public class HttpEntity {

    private String url;
    private String host;
    private Integer responseCode;
    private String html;

    public HttpEntity(String url, String host, Integer responseCode, String html) {
        this.url = url;
        this.host = host;
        this.responseCode = responseCode;
        this.html = html;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
