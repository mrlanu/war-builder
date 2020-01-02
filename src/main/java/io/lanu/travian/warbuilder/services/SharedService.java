package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface SharedService {
    HtmlPage getPage(String url);
    void login();
    void logout();
    boolean isLoggedOut();
    String getCookie();
    String getServer();
    HtmlPage getpSPage();
}
