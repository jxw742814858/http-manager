package cn.iamtudou.entity;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuth extends Authenticator {
    private String account;
    private String passwd;

    public ProxyAuth(String account, String passwd) {
        this.account = account;
        this.passwd = passwd;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(account, passwd.toCharArray());
    }
}
