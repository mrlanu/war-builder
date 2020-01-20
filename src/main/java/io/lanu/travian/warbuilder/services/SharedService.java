package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface SharedService {
    HtmlPage getPage(String url);
    boolean login();
    void logout();
    boolean isLoggedOut();
    String getCookie();
    String getServer();
    HtmlPage getpSPage();
}
