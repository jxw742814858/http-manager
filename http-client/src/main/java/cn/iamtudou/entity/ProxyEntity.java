package cn.iamtudou.entity;

import java.io.Serializable;

public class ProxyEntity implements Serializable {

    private String host;
    private Integer port;
    private String account;
    private String passwd;

    public ProxyEntity(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public ProxyEntity(String host, Integer port, String account, String passwd) {
        this.host = host;
        this.port = port;
        this.account = account;
        this.passwd = passwd;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
